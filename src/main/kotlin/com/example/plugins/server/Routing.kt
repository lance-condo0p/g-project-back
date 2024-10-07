package com.example.plugins.server

import com.example.adapters.CallableAi
import com.example.routes.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.routing.*

fun Application.configureRouting(aiAdapter: CallableAi) {
    routing {
        authenticate("auth-basic") {
            commandRequest(aiAdapter)
            // ToDo: to del test endpoint
            transcribeRequest(aiAdapter)
            // ToDo: to del test endpoint
            gptRequest(aiAdapter)
        }
        openAPI(path = "$URL_NAME/openapi", swaggerFile = "openapi/documentation.yaml")
    }
}
