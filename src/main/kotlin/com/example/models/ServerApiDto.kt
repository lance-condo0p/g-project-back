package com.example.models

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