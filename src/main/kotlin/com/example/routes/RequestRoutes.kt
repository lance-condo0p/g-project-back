package com.example.routes

import com.example.models.MyRequest
import com.example.models.requestsStorage
import com.example.plugins.sendRequest
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.cio.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking

fun Route.listAllRequests() = route("/my_request") {
    get {
        if (requestsStorage.isNotEmpty()) {
            call.respond(requestsStorage)
        } else {
            call.respondText("Empty!", status = HttpStatusCode.OK)
        }
    }
}

fun Route.getRequestById() = route("/my_request") {
    get("{id?}") {
        val id = call.parameters["id"] ?: return@get call.respondText(
            text = "Missing id",
            status = HttpStatusCode.BadRequest
        )
        val requestObject = requestsStorage.find { it.id == id } ?: return@get call.respondText(
            text = "No data with such id = $id",
            status = HttpStatusCode.NotFound
        )
        call.respond(requestObject)
    }
}

fun Route.createRequest() =  route("/my_request") {
    post {
        val requestObject = call.receive<MyRequest>()
        requestsStorage.add(requestObject)
        call.respondText(
            text = "Item added successfully",
            status = HttpStatusCode.Created
        )
    }
}

fun Route.deleteRequest() =  route("/my_request") {
    delete("{id?}") {
        val id = call.parameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
        if (requestsStorage.removeIf { it.id == id }) {
            call.respondText(
                text = "Item removed successfully",
                status = HttpStatusCode.OK
            )
        } else {
            call.respondText(
                text = "Not found",
                status = HttpStatusCode.NotFound
            )
        }
    }
}

fun Route.proxyRequest(client: HttpClient) = route("/proxy") {
    post {
        val isProxyRequest: Boolean = call.parameters["is_proxy_request"].toBoolean()
        if (isProxyRequest) {

            val response = runBlocking {
                sendRequest(client)
            }

            call.respondText(
                text = "Request forwarded. Here is the SC: {${response.status}}",
                status = HttpStatusCode.OK
            )
        } else {
            call.respondText(
                text = "Please use is_proxy_request = true",
                status = HttpStatusCode.BadGateway
            )
        }
    }
}