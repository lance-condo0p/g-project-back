package com.example.routes

import com.example.adapters.AdapterType
import com.example.adapters.getAIResponse
import com.example.models.*
import io.ktor.client.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking

const val URL_VERSION = "/v1"
const val URL_NAME = "/api"
const val URL_PREFIX = "$URL_NAME$URL_VERSION"

fun Route.transcribeRequest(client: HttpClient, aiAdapterType: AdapterType) =
    route("$URL_PREFIX/transcribe") {
        post {
            val request = call.receive<TranscriptVoiceRequest>()
            val response = transcribe(client, aiAdapterType, request.format, request.fileBase64)

            if (response.isNotEmpty()) {
                call.respond(HttpStatusCode.OK, TranscriptVoiceResponse(isSuccessful = true, transcription = response))
            } else {
                call.respond(HttpStatusCode.BadRequest, TranscriptVoiceResponse(isSuccessful = false))
            }
        }
    }

fun Route.commandRequest(client: HttpClient, aiAdapterType: AdapterType) =
    route("$URL_PREFIX/command") {
        post {
            val request = call.receive<TranscriptVoiceRequest>()
            val response = transcribe(client, aiAdapterType, request.format, request.fileBase64)

            if (response.isNotEmpty()) {
                val commandResponse = CommandResponse(wasRecognized = true, transcription = response)
                val lexemes = response.split(" ", ignoreCase = true)
                loop@ for (i in lexemes.indices) {
                    when (CommandType.values().findLast { it.types.contains(lexemes[i].lowercase()) }) {
                        CommandType.DICE -> {
                            if (i < lexemes.size - 1) {
                                val supposedNumber = lexemes[i + 1].toIntOrNull()
                                if(supposedNumber != null) {
                                    commandResponse.commandType = CommandType.DICE
                                    commandResponse.commandResult = (1..supposedNumber).random()
                                    break@loop
                                }
                            }
                        }
                        CommandType.CHARACTER -> {
                            if (i < lexemes.size - 1) {
                                val characterType = lexemes[i + 1]
                                commandResponse.commandType = CommandType.CHARACTER
                                commandResponse.commandResult = "$characterType is an awesome creature with power over than 9000!"
                                break@loop
                            }
                        }
                        else -> {
                            commandResponse.commandType = CommandType.UNKNOWN
                        }
                    }
                }
                call.respond(HttpStatusCode.OK, commandResponse)
            } else {
                call.respond(HttpStatusCode.BadRequest, CommandResponse())
            }
        }
    }

private fun transcribe(client: HttpClient, aiAdapterType: AdapterType, format: VoiceFormat, fileBody: String): String {
    var response = ""
    when (format) {
        VoiceFormat.OGG -> {
            response =
                runBlocking {
                    getAIResponse(client, aiAdapterType, fileBody)
                }
        }
    }
    return response
}