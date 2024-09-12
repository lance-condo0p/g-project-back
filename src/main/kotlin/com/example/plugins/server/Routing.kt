package com.example.plugins.server

import com.example.adapters.AdapterType
import com.example.routes.*
import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.routing.*

fun Application.configureRouting(client: HttpClient, aiAdapterType: AdapterType) {
    routing {
        authenticate("auth-basic") {
            transcribeRequest(client, aiAdapterType)
            commandRequest(client, aiAdapterType)
        }
        openAPI(path = "$URL_NAME/openapi", swaggerFile = "openapi/documentation.yaml")
    }
}
