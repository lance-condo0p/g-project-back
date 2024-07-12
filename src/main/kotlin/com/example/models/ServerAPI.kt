package com.example.models

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

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
    @JsonProperty("file_base64")
    val fileBase64: String,
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class TranscriptVoiceResponse(
    val isSuccessful: Boolean,
    val transcription: String? = null,
)