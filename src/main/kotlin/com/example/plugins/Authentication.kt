package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*

fun Application.configureAuthentication() {
    install(Authentication) {
        basic("auth-basic") {
            realm = "Access to the '/' path"
            validate { credentials ->
                // TODO: implement a more secure algorithm
                if (credentials.name == "foo" && credentials.password == "bar") {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }
}