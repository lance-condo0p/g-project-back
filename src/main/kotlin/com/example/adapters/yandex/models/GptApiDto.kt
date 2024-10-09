package com.example.adapters.yandex.models

enum class MessageRole {
    system,
    user,
    assistant,
}

data class CompletionOptions(
    val stream: Boolean,
    val temperature: Double,
    val maxTokens: String,
)

data class Message(
    val role: MessageRole = MessageRole.system,
    val text: String,
)

data class Alternative(
    val message: Message,
    val status: String,
)

data class ResponseResult(
    val alternatives: List<Alternative>,
    val usage: UsageStatistics,
    val modelVersion: String,
)

data class UsageStatistics(
    val inputTextTokens: String,
    val completionTokens: String,
    val totalTokens: String,
)

data class YandexGPTRequest(
    val modelUri: String,
    val completionOptions: CompletionOptions,
    val messages: List<Message>,
)

/**
 * Examples:
 *
 *   "result": {
 *      "alternatives": [
 *          {
 *              "message": {
 *                  "role":"assistant",
 *                  "text":"xxx"
 *              },
 *              "status":"ALTERNATIVE_STATUS_FINAL"
 *           }
 *      ],
 *      "usage": {
 *          "inputTextTokens":"85",
 *          "completionTokens":"326",
 *          "totalTokens":"411"
 *      },
 *      "modelVersion":"22.05.2024"
 *   }
 *
 *    "error": {
 *       "grpcCode":3,
 *       "httpCode":400,
 *       "message":"Specified folder ID 'xxx' does not match with service account folder ID 'yyy'",
 *       "httpStatus":"Bad Request",
 *       "details":[]
 *   }
 */
data class YandexGPTResponse(
    val result: ResponseResult,
)
