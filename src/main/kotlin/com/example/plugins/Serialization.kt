package com.example.plugins

import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        /* TODO: configure serialization here via "jackson {}" instead of annotations in DTO.
          See https://github.com/ktorio/ktor-samples/blob/1.3.0/feature/jackson/src/JacksonApplication.kt
         */
        jackson()
    }
}