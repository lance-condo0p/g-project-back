package com.example.models

//import kotlinx.serialization.Serializable

//@Serializable
data class MyRequest(
    val id: String,
    val data: String
)

val requestsStorage = mutableListOf<MyRequest>()