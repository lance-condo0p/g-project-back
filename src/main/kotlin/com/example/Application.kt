package com.example

import com.example.adapters.AdapterType
import com.example.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    val aiAdapterType = AdapterType.valueOf(environment.config.propertyOrNull("ktor.application.ai_adapter")?.getString() ?: AdapterType.Yandex.toString())

    val client = configureClient()
    configureRouting(
        client = client,
        aiAdapterType = aiAdapterType
    )
    configureSerialization()
    configureLogging()

//    client.close()
}