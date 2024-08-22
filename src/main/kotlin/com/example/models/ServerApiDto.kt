package com.example.models

enum class VoiceFormat {
    OGG,
}

/**
 * All set items must be lowercase
 */
enum class CommandType(
    val types: HashSet<String>,
) {
    DICE(hashSetOf("кинь", "ким")), // roll a dice
    CHARACTER(hashSetOf("дай")), // generate character's description
    UNKNOWN(hashSetOf("-")), // unrecognized / unsupported command
}

data class TranscriptVoiceRequest(
    val format: VoiceFormat,
    val fileBase64: String,
)

data class TranscriptVoiceResponse(
    val isSuccessful: Boolean,
    val transcription: String? = null,
)

data class CommandRequest(
    val format: VoiceFormat,
    val fileBase64: String,
)

data class CommandResponse(
    val wasRecognized: Boolean = false,
    val transcription: String? = null,
    var commandType: CommandType? = null,
    var commandResult: Any? = null,
)
