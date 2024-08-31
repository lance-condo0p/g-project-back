package com.example.plugins.server

import com.example.adapters.AdapterType
import com.example.routes.*
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.routing.*

fun Application.configureRouting(client: HttpClient, aiAdapterType: AdapterType) {
    client.config {
        install(Logging) {
            // TODO: should be based on logback config
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            jackson()
        }
    }
    routing {
        authenticate("auth-basic") {
            transcribeRequest(client, aiAdapterType)
            commandRequest(client, aiAdapterType)
        }
        openAPI(path = "$URL_NAME/openapi", swaggerFile = "openapi/documentation.yaml")
    }
}
