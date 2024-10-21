package com.example.models

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

data class Character(
    val description: String,
    val level: Int,
    val strength: Int,
    val dexterity: Int,
    val constitution: Int,
    val intelligence: Int,
    val wisdom: Int,
    val charisma: Int,
)

data class TranscriptVoiceRequest(
    val format: String, // enum: OGG
    val fileBase64: String,
)

data class TranscriptVoiceResponse(
    val isSuccessful: Boolean,
    val transcription: String? = null,
)

data class CommandRequest(
    val format: String, // enum: OGG
    val fileBase64: String,
)

data class CommandResponse(
    val wasRecognized: Boolean = false,
    val transcription: String? = null,
    var commandType: CommandType? = null,
    var commandResult: Any? = null,
)

data class CommandResponseCharacter(
    val wasRecognized: Boolean = false,
    val transcription: String? = null,
    var commandType: CommandType? = null,
    var commandResult: Character? = null,
)
