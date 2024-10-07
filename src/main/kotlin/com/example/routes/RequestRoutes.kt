package com.example.routes

import com.example.adapters.CallableAi
import com.example.models.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

const val URL_VERSION = "/v1"
const val URL_NAME = "/api"
const val URL_PREFIX = "$URL_NAME$URL_VERSION"

fun Route.transcribeRequest(aiAdapter: CallableAi) =
    route("$URL_PREFIX/transcribe") {
        post {
            val request = call.receive<TranscriptVoiceRequest>()
            val response = aiAdapter.transcribe(request.format, request.fileBase64).result

            if (response.isNotEmpty()) {
                call.respond(HttpStatusCode.OK, TranscriptVoiceResponse(isSuccessful = true, transcription = response))
            } else {
                call.respond(HttpStatusCode.BadRequest, TranscriptVoiceResponse(isSuccessful = false))
            }
        }
    }

fun Route.gptRequest(aiAdapter: CallableAi) =
    route("$URL_PREFIX/gpt") {
        post {
            val request = call.receive<TranscriptVoiceRequest>()
            val response = aiAdapter.askGpt(request.fileBase64)

            if (response.isSuccessful) {
                call.respond(HttpStatusCode.OK, TranscriptVoiceResponse(isSuccessful = true, transcription = response.result))
            } else {
                call.respond(HttpStatusCode.BadRequest, TranscriptVoiceResponse(isSuccessful = false))
            }
        }
    }

fun Route.commandRequest(aiAdapter: CallableAi) =
    route("$URL_PREFIX/command") {
        post {
            val request = call.receive<TranscriptVoiceRequest>()
            val response = aiAdapter.transcribe(request.format, request.fileBase64).result

            if (response.isNotEmpty()) {
                val commandResponse = CommandResponse(wasRecognized = true, transcription = response)
                val lexemes = response.split(" ", ignoreCase = true)
                loop@ for (i in lexemes.indices) {
                    when (CommandType.values().findLast { it.types.contains(lexemes[i].lowercase()) }) {
                        CommandType.DICE -> {
                            if (i < lexemes.size - 1) {
                                val supposedNumber = lexemes[i + 1].toIntOrNull()
                                if (supposedNumber != null) {
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
                                val gptResponse = aiAdapter.askGpt(characterType).result
                                commandResponse.commandResult =
                                    Character(
                                        description = gptResponse,
                                        level = (1..10).random(),
                                        strength = (1..10).random(),
                                        dexterity = (1..10).random(),
                                        constitution = (1..10).random(),
                                        intelligence = (1..10).random(),
                                        wisdom = (1..10).random(),
                                        charisma = (1..10).random(),
                                    )
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
