package com.example.tests.integration

import com.example.asResource
import com.example.model.Command
import com.example.models.*
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.testing.*
import io.ktor.test.dispatcher.*
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.test.*

class TranscribeIT {
    @Test
    fun `Send transcribe request with wrong file body`() = testSuspend {
        testClient.post("/api/v1/transcribe") {
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

    @Test
    fun `Send transcribe request with short correct body`() =
        testSuspend {
            val voiceCommand = objectMapper.readValue<Command>("/commands/privet.json".asResource())

            testClient.post("/api/v1/transcribe") {
                basicAuth(username = "foo", password = "bar")
                contentType(ContentType.Application.Json)
                setBody<TranscriptVoiceRequest>(TranscriptVoiceRequest(format = VoiceFormat.OGG, fileBase64 = voiceCommand.file))
            }.apply {
                assertEquals(HttpStatusCode.OK, status)
                val response = body<TranscriptVoiceResponse>()
                assertTrue(response.isSuccessful)
                assertEquals(voiceCommand.text, response.transcription)
            }
        }

    @Test
    fun `Send transcribe request with long correct body`() =
        testSuspend {
            val voiceCommand = objectMapper.readValue<Command>("/commands/test_odin_dva_tri_chetire_shest.json".asResource())

            testClient.post("/api/v1/transcribe") {
                basicAuth(username = "foo", password = "bar")
                contentType(ContentType.Application.Json)
                setBody<TranscriptVoiceRequest>(TranscriptVoiceRequest(format = VoiceFormat.OGG, fileBase64 = voiceCommand.file))
            }.apply {
                assertEquals(HttpStatusCode.OK, status)
                val response = body<TranscriptVoiceResponse>()
                assertTrue(response.isSuccessful)
                assertEquals(voiceCommand.text, response.transcription)
            }
        }

    companion object {
        private lateinit var testApp: TestApplication
        lateinit var testClient: HttpClient

        val objectMapper = jacksonObjectMapper()

        @JvmStatic
        @BeforeAll
        fun setup() {
            testApp = TestApplication { }
            testClient =
                testApp.createClient {
                    install(ContentNegotiation) {
                        jackson {
                            setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE) // use snake_case
                            setSerializationInclusion(JsonInclude.Include.NON_NULL) // ignore NULL attributes
                        }
                    }
                }
        }

        @JvmStatic
        @AfterAll
        fun teardown() {
            testApp.stop()
        }
    }
}
