package com.example

import com.example.adapters.AiAdapter
import com.example.plugins.server.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain.main as startNettyEngine

fun main(args: Array<String>): Unit = startNettyEngine(args)

private val applicationHttpClient =
    HttpClient(CIO) {
        install(Logging) {
            // TODO: should be based on logback config
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            jackson()
        }
    }

fun Application.module(client: HttpClient = applicationHttpClient) {
    val adapterTypeValue = environment.config.propertyOrNull("ktor.application.ai_adapter")?.getString()
    val aiAdapter = AiAdapter.getInstance(client, adapterTypeValue)

    configureAuthentication()

    val isLoggingEnabled =
        environment.config
            .propertyOrNull("ktor.application.is_logging_enabled")
            ?.getString()
            .toBoolean()
    if (isLoggingEnabled) {
        configureLogging()
    }
    configureSerialization()
    configureRouting(aiAdapter = aiAdapter)
}
