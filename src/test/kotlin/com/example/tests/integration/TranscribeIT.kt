package com.example.tests.integration

import com.example.asResource
import com.example.model.Command
import com.example.models.*
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.test.dispatcher.*
import org.junit.jupiter.api.Test
import kotlin.test.*

class TranscribeIT : IntegrationTestStarter() {
    @Test
    fun `Send transcribe request with wrong file body`() =
        testSuspend {
            testClient
                .post("/api/v1/transcribe") {
                    basicAuth(username = "foo", password = "bar")
                    contentType(ContentType.Application.Json)
                    setBody<TranscriptVoiceRequest>(TranscriptVoiceRequest(format = "OGG", fileBase64 = "1234"))
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

            testClient
                .post("/api/v1/transcribe") {
                    basicAuth(username = "foo", password = "bar")
                    contentType(ContentType.Application.Json)
                    setBody<TranscriptVoiceRequest>(TranscriptVoiceRequest(format = "OGG", fileBase64 = voiceCommand.file))
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

            testClient
                .post("/api/v1/transcribe") {
                    basicAuth(username = "foo", password = "bar")
                    contentType(ContentType.Application.Json)
                    setBody<TranscriptVoiceRequest>(TranscriptVoiceRequest(format = "OGG", fileBase64 = voiceCommand.file))
                }.apply {
                    assertEquals(HttpStatusCode.OK, status)
                    val response = body<TranscriptVoiceResponse>()
                    assertTrue(response.isSuccessful)
                    assertEquals(voiceCommand.text, response.transcription)
                }
        }
}
