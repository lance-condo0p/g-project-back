package com.example

import com.example.model.Command
import com.example.models.TranscriptVoiceRequest
import com.example.models.TranscriptVoiceResponse
import com.example.models.VoiceFormat
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
import org.junit.AfterClass
import org.junit.BeforeClass
import kotlin.test.*

class ApplicationTest {
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

    @Test
    fun `Throw dice with wrong command`() =
        testSuspend {
            val voiceCommand = objectMapper.readValue<Command>("/commands/kin_kubik.json".asResource())

            testClient.post("/api/v1/command") {
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
    fun `Throw dice up to 18 with correct command`() =
        testSuspend {
            val voiceCommand = objectMapper.readValue<Command>("/commands/kin_18.json".asResource())

            testClient.post("/api/v1/command") {
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
    fun `Throw dice up to 10 with noise and correct command`() =
        testSuspend {
            val voiceCommand = objectMapper.readValue<Command>("/commands/eshe_erunda_kin_10.json".asResource())

            testClient.post("/api/v1/command") {
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
    fun `Generate Elf description with correct command`() =
        testSuspend {
            val voiceCommand = objectMapper.readValue<Command>("/commands/dai_elfa.json".asResource())

            testClient.post("/api/v1/command") {
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

    // TODO: to define what is a right syntax - "bla-bla command" or "command bla-bla"?
    @Test
    fun `Generate Ork description with noise from another action and correct command`() =
        testSuspend {
            val voiceCommand = objectMapper.readValue<Command>("/commands/kin_dai_orka.json".asResource())

            testClient.post("/api/v1/command") {
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
        @BeforeClass
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
        @AfterClass
        fun teardown() {
            testApp.stop()
        }
    }

    private fun String.asResource(): String = object {}.javaClass.getResource(this).readText()
}
