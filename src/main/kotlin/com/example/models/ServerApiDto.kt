package com.example.models

data class MyRequest(
    val id: String,
    val data: String,
)

val requestsStorage = mutableListOf<MyRequest>()

enum class VoiceFormat {
    OGG,
}

data class TranscriptVoiceRequest(
    val format: VoiceFormat,
    val fileBase64: String,
)

data class TranscriptVoiceResponse(
    val isSuccessful: Boolean,
    val transcription: String? = null,
)