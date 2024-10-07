package com.example.adapters.yandex

import com.example.adapters.AiAdapterResponse
import com.example.adapters.CallableAi
import com.example.adapters.yandex.models.*
import io.ktor.client.HttpClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.apache.commons.codec.binary.Base64

enum class YandexVoiceFormats {
    OGG,
}

class YandexAdapter(
    private val client: HttpClient,
) : CallableAi {
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
            return AiAdapterResponse(false, "Opps :(")
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
            val goodResponse = response.body<YandexResponse>().result
            AiAdapterResponse(true, goodResponse)
        } else {
            // ToDO: bad response
            AiAdapterResponse(false, "Opps :(")
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
                        completionOptions = YandexCompletionOptions(),
                        messages =
                            listOf(
                                YandexMessage(
                                    role = YandexRole.system,
                                    text = GPT_SYSTEM,
                                ),
                                YandexMessage(
                                    role = YandexRole.user,
                                    text = gptCommand,
                                ),
                            ),
                    ),
                )
            }
        return if (response.status == HttpStatusCode.OK) {
            val goodResponse =
                response
                    .body<YandexGPTResponse>()
                    .result.alternatives
                    .findLast { it.message.role == YandexRole.assistant }
                    ?.message
                    ?.text
            if (goodResponse != null) {
                AiAdapterResponse(true, goodResponse)
            } else {
                // ToDO: bad response
                AiAdapterResponse(false, "Opps :(")
            }
        } else {
            // ToDO: bad response
            AiAdapterResponse(false, "Opps :(")
        }
    }
}
