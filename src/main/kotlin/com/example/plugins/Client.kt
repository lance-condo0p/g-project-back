package com.example.plugins

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import java.io.File

fun configureClient(): HttpClient = HttpClient(CIO) {
    install(Logging) {
        level = LogLevel.ALL
    }
    install(ContentNegotiation) {
        jackson()
    }
}

/**
 * curl --request POST \
 *   --url https://api.openai.com/v1/audio/transcriptions \
 *   --header "Authorization: Bearer $OPENAI_API_KEY" \
 *   --header 'Content-Type: multipart/form-data' \
 *   --form file=@/path/to/file/audio.mp3 \
 *   --form model=whisper-1
 */
suspend fun sendRequest(client: HttpClient): HttpResponse = client.request {
    method = HttpMethod.Post
    url {
        protocol = URLProtocol.HTTPS
        host = "api.openai.com"
        path("v1/audio/transcriptions")
    }
    headers {
        append(HttpHeaders.Authorization, "Bearer ${System.getenv("OPENAI_API_KEY")}")
    }
    contentType(ContentType.MultiPart.FormData)
    setBody(
        MultiPartFormDataContent(
            formData {
                append("model", "whisper-1")
                append("file", File("/Users/grigory/to_del/test.m4a").readBytes())
            }
        )
    )
}