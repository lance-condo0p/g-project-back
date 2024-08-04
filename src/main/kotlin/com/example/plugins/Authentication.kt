package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*

fun Application.configureAuthentication() {
    install(Authentication) {
        basic("auth-basic") {
            realm = "Access to the '/' path"
            validate { credentials ->
                // TODO: implement even more secure algorithm
                if (credentials.name == System.getenv("CREDENTIALS_DB_NAME") &&
                    credentials.password == System.getenv("CREDENTIALS_DB_PASSWORD")
                ) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }
}