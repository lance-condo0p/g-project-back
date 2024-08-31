package com.example.plugins.client

import io.ktor.client.*
import io.ktor.client.plugins.logging.*

fun configureClientLogging(client: HttpClient) = client.config {
    install(Logging) {
        // TODO: should be based on logback config
        level = LogLevel.ALL
    }
}