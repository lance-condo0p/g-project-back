package com.example.plugins

import com.example.routes.*
import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(client: HttpClient) {
    routing {
        listAllRequests()
        getRequestById()
        createRequest()
        deleteRequest()
        proxyRequest(client)
    }
}