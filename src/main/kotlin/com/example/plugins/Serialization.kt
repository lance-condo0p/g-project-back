package com.example.plugins

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        // Configured using https://github.com/ktorio/ktor-samples/blob/1.3.0/feature/jackson/src/JacksonApplication.kt
        jackson {
            setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE) // use snake_case
            setSerializationInclusion(JsonInclude.Include.NON_NULL) // ignore NULL attributes
//            configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true) // enable @JsonRootName annotation
        }
    }
}