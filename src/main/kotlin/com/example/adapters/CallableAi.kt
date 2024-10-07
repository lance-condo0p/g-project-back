package com.example.adapters

interface CallableAi {
    fun isSupportedFileFormat(fileFormat: String): Boolean

    suspend fun transcribe(
        fileFormat: String,
        fileBody: String,
    ): AiAdapterResponse

    suspend fun askGpt(gptCommand: String): AiAdapterResponse
}
