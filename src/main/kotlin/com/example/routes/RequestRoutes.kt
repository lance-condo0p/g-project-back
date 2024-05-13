package com.example.routes

import com.example.models.MyRequest
import com.example.models.requestsStorage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.myRequestsRouting() {
    route("/my_request") {
        get {
            if (requestsStorage.isNotEmpty()) {
                call.respond(requestsStorage)
            } else {
                call.respondText("Empty!", status = HttpStatusCode.OK)
            }
        }
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
        post {
            val requestObject = call.receive<MyRequest>()
            requestsStorage.add(requestObject)
            call.respondText(
                text = "Item added successfully",
                status = HttpStatusCode.Created
            )
        }
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
}