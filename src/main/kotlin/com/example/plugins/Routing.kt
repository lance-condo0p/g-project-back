package com.example.plugins

import com.example.adapters.AdapterType
import com.example.routes.*
import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(client: HttpClient, aiAdapterType: AdapterType) {
    routing {
        listAllRequests()
        getRequestById()
        createRequest()
        deleteRequest()
        proxyRequest(client, aiAdapterType)
    }
}