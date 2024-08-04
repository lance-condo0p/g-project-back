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

            when (request.format) {
                VoiceFormat.OGG -> {
                    val response =
                        runBlocking {
                            getAIResponse(client, aiAdapterType, request.fileBase64)
                        }
                    if (response.isNotEmpty()) {
                        call.respond(HttpStatusCode.OK, TranscriptVoiceResponse(isSuccessful = true, transcription = response))
                    } else {
                        call.respond(HttpStatusCode.BadRequest, TranscriptVoiceResponse(isSuccessful = false))
                    }
                }
            }
        }
    }
