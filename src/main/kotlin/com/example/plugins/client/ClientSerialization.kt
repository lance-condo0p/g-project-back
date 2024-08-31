package com.example.plugins.client

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.jackson.*

fun configureClientSerialization(client: HttpClient) = client.config {
    install(ContentNegotiation) {
        jackson()
    }
}