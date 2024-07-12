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

const val URL_VERSION_PREFIX = "/v1"

fun Route.listAllRequests() = route("$URL_VERSION_PREFIX/my_request") {
    get {
        if (requestsStorage.isNotEmpty()) {
            call.respond(requestsStorage)
        } else {
            call.respondText("Empty!", status = HttpStatusCode.OK)
        }
    }
}

fun Route.getRequestById() = route("$URL_VERSION_PREFIX/my_request") {
    get("{id?}") {
        val id = call.parameters["id"] ?: return@get call.respondText(
            text = "Missing id",
            status = HttpStatusCode.BadRequest,
        )
        val requestObject = requestsStorage.find { it.id == id } ?: return@get call.respondText(
            text = "No data with such id = $id",
            status = HttpStatusCode.NotFound,
        )
        call.respond(requestObject)
    }
}

fun Route.createRequest() = route("$URL_VERSION_PREFIX/my_request") {
    post {
        val requestObject = call.receive<MyRequest>()
        requestsStorage.add(requestObject)
        call.respondText(
            text = "Item added successfully",
            status = HttpStatusCode.Created,
        )
    }
}

fun Route.deleteRequest() = route("$URL_VERSION_PREFIX/my_request") {
    delete("{id?}") {
        val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
        if (requestsStorage.removeIf { it.id == id }) {
            call.respondText(
                text = "Item removed successfully",
                status = HttpStatusCode.OK,
            )
        } else {
            call.respondText(
                text = "Not found",
                status = HttpStatusCode.NotFound,
            )
        }
    }
}

fun Route.proxyRequest(client: HttpClient, aiAdapterType: AdapterType) =
    route("$URL_VERSION_PREFIX/proxy") {
        post {
            val isProxyRequest: Boolean = call.parameters["is_proxy_request"].toBoolean()

            if (isProxyRequest) {
                call.respondText(
                    text = "Request forwarded.",
                    status = HttpStatusCode.OK,
                )
            } else {
                call.respondText(
                    text = "Please use is_proxy_request = true",
                    status = HttpStatusCode.BadGateway,
                )
            }
        }
    }

fun Route.transcribeRequest(client: HttpClient, aiAdapterType: AdapterType) =
    route("$URL_VERSION_PREFIX/transcribe") {
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
