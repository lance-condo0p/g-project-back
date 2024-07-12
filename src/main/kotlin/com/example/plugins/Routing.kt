package com.example.plugins

import com.example.adapters.AdapterType
import com.example.routes.*
import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.plugins.openapi.*

fun Application.configureRouting(client: HttpClient, aiAdapterType: AdapterType) {
    routing {
        authenticate("auth-basic") {
            listAllRequests()
            getRequestById()
            createRequest()
            deleteRequest()
            proxyRequest(client, aiAdapterType)
            transcribeRequest(client, aiAdapterType)
        }
        openAPI(path = "openapi", swaggerFile = "openapi/documentation.yaml")
    }
}