package com.example.adapters.yandex.models

const val COMPLETION_OPT_TEMPERATURE = 0.8
const val COMPLETION_OPT_MAX_TOKENS = "2000"
const val COMPLETION_OPT_STREAM = false

const val GPT_SYSTEM =
    "Ты - gamemaster фэнтезийной ролевой игры. " +
        "Придумай монстра, его уникальное имя, внешний вид и способности." +
        "Не используй Markdown!"

enum class YandexRole {
    system,
    user,
    assistant,
}

data class YandexCompletionOptions(
    val stream: Boolean = COMPLETION_OPT_STREAM,
    val temperature: Double = COMPLETION_OPT_TEMPERATURE,
    val maxTokens: String = COMPLETION_OPT_MAX_TOKENS,
)

data class YandexMessage(
    val role: YandexRole = YandexRole.system,
    val text: String,
)

data class YandexResponseAlternative(
    val message: YandexMessage,
    val status: String,
)

data class YandexResponseResult(
    val alternatives: List<YandexResponseAlternative>,
    val usage: YandexUsageStatistics,
    val modelVersion: String,
)

data class YandexUsageStatistics(
    val inputTextTokens: String,
    val completionTokens: String,
    val totalTokens: String,
)

data class YandexGPTRequest(
    val modelUri: String,
    val completionOptions: YandexCompletionOptions,
    val messages: List<YandexMessage>,
)

/**
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
 */
data class YandexGPTResponse(
    val result: YandexResponseResult,
)

/**
 *   "error": {
 *      "grpcCode":3,
 *      "httpCode":400,
 *      "message":"Specified folder ID 'xxx' does not match with service account folder ID 'yyy'",
 *      "httpStatus":"Bad Request",
 *      "details":[]
 *  }
 */
data class YandexGPTError(
    val error: String,
)
