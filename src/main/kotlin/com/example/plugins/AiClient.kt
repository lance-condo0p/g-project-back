package com.example.plugins

import com.assemblyai.api.AssemblyAI
import com.assemblyai.api.resources.transcripts.types.TranscriptOptionalParams
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.util.cio.*
import java.io.File
import java.util.*

fun configureClient(): HttpClient = HttpClient(CIO) {
    install(Logging) {
        level = LogLevel.ALL
    }
    install(ContentNegotiation) {
        jackson()
    }
}

/**
 * curl --request POST \
 *   --url https://api.openai.com/v1/audio/transcriptions \
 *   --header "Authorization: Bearer $OPENAI_API_KEY" \
 *   --header 'Content-Type: multipart/form-data' \
 *   --form file=@/path/to/file/audio.mp3 \
 *   --form model=whisper-1
 */
suspend fun sendRequestOpenAi(client: HttpClient): HttpResponse = client.request {
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
                append("file", File("/Users/grigory/to_del/test.m4a").readBytes())
            }
        )
    )
}

/**
 * https://www.assemblyai.com/app
 */
fun sendRequestAssemblyAi(): Optional<String>? {
    val client: AssemblyAI = AssemblyAI.builder()
        .apiKey(System.getenv("ASSEMBLYAI_API_KEY"))
        .build()

    val audioUrl = "https://storage.googleapis.com/aai-web-samples/5_common_sports_injuries.mp3"

    val params = TranscriptOptionalParams.builder()
        .speakerLabels(true)
        .build()

    val transcript = client.transcripts().transcribe(audioUrl, params)

    return transcript.text

//    transcript.utterances.ifPresent { utterances: List<TranscriptUtterance> ->
//        utterances.forEach(
//            Consumer { utterance: TranscriptUtterance -> println("Speaker " + utterance.speaker + ": " + utterance.text) }
//        )
//    }
}

/**
 * https://yandex.cloud/ru/docs/speechkit/quickstart/stt-quickstart-v1
 *
 * export FOLDER_ID=<идентификатор_каталога>
 * export IAM_TOKEN=<IAM-токен>
 * curl -X POST \
 *    -H "Authorization: Bearer ${IAM_TOKEN}" \
 *    --data-binary "@speech.ogg" \
 *    "https://stt.api.cloud.yandex.net/speech/v1/stt:recognize?folderId=${FOLDER_ID}&lang=ru-RU"
 */
suspend fun sendRequestYandexKit(client: HttpClient): HttpResponse = client.request {
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
    setBody(
        File("/Users/grigory/to_del/test.ogg").readChannel()
    )
}