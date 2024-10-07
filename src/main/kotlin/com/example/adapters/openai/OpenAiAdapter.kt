package com.example.adapters.openai

import com.example.adapters.AiAdapterResponse
import com.example.adapters.CallableAi
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import org.apache.commons.codec.binary.Base64

enum class OpenAiVoiceFormats {
    MP3,
    MP4,
    MPEG,
    MPGA,
    M4A,
    WAV,
    WEBM,
}

class OpenAiAdapter(
    private val client: HttpClient,
) : CallableAi {
    override fun isSupportedFileFormat(fileFormat: String): Boolean = enumValues<OpenAiVoiceFormats>().any { it.name == fileFormat }

    /**
     * curl --request POST \
     *   --url https://api.openai.com/v1/audio/transcriptions \
     *   --header "Authorization: Bearer $OPENAI_API_KEY" \
     *   --header 'Content-Type: multipart/form-data' \
     *   --form file=@/path/to/file/audio.mp3 \
     *   --form model=whisper-1
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
                    host = "api.openai.com"
                    path("v1/audio/transcriptions")
                }
                headers {
                    append(HttpHeaders.Authorization, "Bearer ${System.getenv("OPENAI_API_KEY")}")
                }
                contentType(ContentType.MultiPart.FormData)
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("model", "whisper-1")
                            append("file", fileAsByteArray)
                        },
                    ),
                )
            }
        return AiAdapterResponse(true, response.toString())
    }

    override suspend fun askGpt(gptCommand: String): AiAdapterResponse {
        TODO("Not yet implemented")
    }
}
