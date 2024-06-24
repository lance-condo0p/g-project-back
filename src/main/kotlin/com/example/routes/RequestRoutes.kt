package com.example.routes

import com.example.models.MyRequest
import com.example.models.YandexResponse
import com.example.models.requestsStorage
import com.example.plugins.sendRequestYandexKit
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.http.*
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
            // #1
//            val response = runBlocking {
//                sendRequestOpenAi(client)
//            }
//
//            call.respondText(
//                text = "Request forwarded. Here is the SC: {${response.status}}",
//                status = HttpStatusCode.OK
//            )

            // #2
//            val response = runBlocking {
//                sendRequestAssemblyAi()
//            }
//            call.respondText(
//                text = response?.get() ?: "none",
//                status = HttpStatusCode.OK
//            )

            // #3
            val response = runBlocking {
                sendRequestYandexKit(client)
            }

            val responseObj: YandexResponse = response.body()

            call.respondText(
                text = "Request forwarded. SC is: ${response.status}, Text is: ${responseObj.result}",
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