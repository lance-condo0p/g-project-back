package com.example.adapters.yandex

import com.example.adapters.AiAdapterResponse
import com.example.adapters.CallableAi
import com.example.adapters.yandex.models.*
import io.ktor.client.HttpClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.apache.commons.codec.binary.Base64

internal class YandexAdapter(
    private val client: HttpClient,
) : CallableAi {
    private val COMPLETION_OPT_TEMPERATURE = 0.8
    private val COMPLETION_OPT_MAX_TOKENS = "2000"
    private val COMPLETION_OPT_STREAM = false

    private val SYSTEM_ROLE_CHARACTER_SETUP =
        "Ты - gamemaster фэнтезийной ролевой игры. " +
            "Придумай монстра, его уникальное имя, внешний вид и способности." +
            "Не используй Markdown!"

    enum class YandexVoiceFormats {
        OGG,
    }

    override fun isSupportedFileFormat(fileFormat: String): Boolean = enumValues<YandexVoiceFormats>().any { it.name == fileFormat }

    /**
     * Speech to text API
     * https://yandex.cloud/ru/docs/speechkit/quickstart/stt-quickstart-v1
     *
     * export FOLDER_ID=<идентификатор_каталога>
     * export IAM_TOKEN=<IAM-токен>
     * curl -X POST \
     *    -H "Authorization: Bearer ${IAM_TOKEN}" \
     *    --data-binary "@speech.ogg" \
     *    "https://stt.api.cloud.yandex.net/speech/v1/stt:recognize?folderId=${FOLDER_ID}&lang=ru-RU"
     */
    override suspend fun transcribe(
        fileFormat: String,
        fileBody: String,
    ): AiAdapterResponse {
        if (!isSupportedFileFormat(fileFormat)) {
            return AiAdapterResponse(false)
        }
        val response =
            client.request {
                val fileAsByteArray = Base64().decode(fileBody)

                method = HttpMethod.Post
                url {
                    protocol = URLProtocol.HTTPS
                    host = "stt.api.cloud.yandex.net"
                    path("speech/v1/stt:recognize")
                    parameter("topic", "general")
                }
                headers {
                    append(HttpHeaders.Authorization, "Api-Key ${System.getenv("YANDEX_API_KEY")}")
                }
                setBody(fileAsByteArray)
            }
        return if (response.status == HttpStatusCode.OK) {
            val responseResult = response.body<YandexSpeechResponse>().result
            AiAdapterResponse(true, responseResult)
        } else {
            // ToDO: bad response
            AiAdapterResponse(false)
        }
    }

    /**
     * GPT API
     * https://llm.api.cloud.yandex.net/foundationModels/v1/completion
     *
     * export FOLDER_ID=<идентификатор_каталога>
     * export IAM_TOKEN=<IAM-токен>
     * curl \
     * --request POST \
     * --header "Content-Type: application/json" \
     * --header "Authorization: Bearer ${IAM_TOKEN}" \
     * --header "x-folder-id: ${FOLDER_ID}" \
     * --data "@prompt.json" \
     * "https://llm.api.cloud.yandex.net/foundationModels/v1/completion"
     */
    override suspend fun askGpt(gptCommand: String): AiAdapterResponse {
        val response =
            client.request {
                method = HttpMethod.Post
                url {
                    protocol = URLProtocol.HTTPS
                    host = "llm.api.cloud.yandex.net"
                    path("foundationModels/v1/completion")
                }
                headers {
                    append(HttpHeaders.Authorization, "Api-Key ${System.getenv("YANDEX_GPT_API_KEY")}")
                }
                contentType(ContentType.Application.Json)
                setBody<YandexGPTRequest>(
                    YandexGPTRequest(
                        modelUri = "gpt://${System.getenv("YANDEX_GPT_FOLDER_ID")}/yandexgpt-lite",
                        completionOptions = CompletionOptions(
                                stream = COMPLETION_OPT_STREAM,
                                temperature = COMPLETION_OPT_TEMPERATURE,
                                maxTokens = COMPLETION_OPT_MAX_TOKENS,
                            ),
                        messages =
                            listOf(
                                Message(
                                    role = MessageRole.system,
                                    text = SYSTEM_ROLE_CHARACTER_SETUP,
                                ),
                                Message(
                                    role = MessageRole.user,
                                    text = gptCommand,
                                ),
                            ),
                    ),
                )
            }
        return if (response.status == HttpStatusCode.OK) {
            val responseMessage =
                response
                    .body<YandexGPTResponse>()
                    .result.alternatives
                    .findLast { it.message.role == MessageRole.assistant }
                    ?.message
                    ?.text
            if (responseMessage != null) {
                AiAdapterResponse(true, responseMessage)
            } else {
                // ToDO: bad response
                AiAdapterResponse(false)
            }
        } else {
            // ToDO: bad response
            AiAdapterResponse(false)
        }
    }
}
