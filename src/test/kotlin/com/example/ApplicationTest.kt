package com.example

import com.example.models.TranscriptVoiceRequest
import com.example.models.TranscriptVoiceResponse
import com.example.models.VoiceFormat
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.testing.*
import kotlin.test.*

// TODO: add more tests
class ApplicationTest {
    @Test
    fun `Send transcribe request with wrong file body`() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                jackson {
                    setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE) // use snake_case
                    setSerializationInclusion(JsonInclude.Include.NON_NULL) // ignore NULL attributes
                }
            }
        }
        client.post("/api/v1/transcribe") {
            basicAuth(username = "foo", password = "bar")
            contentType(ContentType.Application.Json)
            setBody<TranscriptVoiceRequest>(TranscriptVoiceRequest(format = VoiceFormat.OGG, fileBase64 = "1234"))
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status)
            val response = body<TranscriptVoiceResponse>()
            assertFalse(response.isSuccessful)
            assertNull(response.transcription)
        }
    }
}
