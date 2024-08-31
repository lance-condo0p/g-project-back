package com.example

import com.example.adapters.AdapterType
import com.example.plugins.client.configureClientLogging
import com.example.plugins.server.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.Application

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

val applicationHttpClient = HttpClient(CIO) {
    install(Logging) {
        // TODO: should be based on logback config
        level = LogLevel.ALL
    }
    install(ContentNegotiation) {
        jackson()
    }
}

fun Application.module(client: HttpClient = applicationHttpClient) {
    val aiAdapterType = AdapterType.valueOf(
        environment.config.propertyOrNull("ktor.application.ai_adapter")?.getString() ?: AdapterType.Yandex.toString()
    )

    configureAuthentication()

    val isLoggingEnabled = environment.config.propertyOrNull("ktor.application.is_logging_enabled")?.getString().toBoolean()
    if (isLoggingEnabled) {
        configureLogging()
    }
    configureSerialization()

    configureRouting(
        client = client,
        aiAdapterType = aiAdapterType,
    )
}
