package com.example.plugins.server

import io.ktor.server.application.*
import io.ktor.server.auth.*

fun Application.configureAuthentication() {
    install(Authentication) {
        basic("auth-basic") {
            realm = "Access to the '/' path"
            validate { credentials ->
                // TODO: implement even more secure algorithm
                if (credentials.name == System.getenv().getOrDefault("CREDENTIALS_DB_NAME","foo") &&
                    credentials.password == System.getenv().getOrDefault("CREDENTIALS_DB_PASSWORD","bar")
                ) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }
}