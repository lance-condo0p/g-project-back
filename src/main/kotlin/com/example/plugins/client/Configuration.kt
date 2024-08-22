package com.example.plugins.client

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.jackson.*

fun configureClient(): HttpClient = HttpClient(CIO) {
    install(Logging) {
        // TODO: should be based on logback config
        level = LogLevel.ALL
    }
    install(ContentNegotiation) {
        jackson()
    }
}