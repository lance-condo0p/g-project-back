package com.example.tests.module

import com.example.adapters.YandexResponse
import com.example.adapters.sendRequestYandexKit
import com.example.asResource
import com.example.model.Command
import com.example.models.*
import com.example.module
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.test.*

@MockKExtension.ConfirmVerification
class CommandTest {
    @Test
    fun `Validate proper call to Yandex API (mocked) - OK`(): Unit = runBlocking {
        val voiceCommand = objectMapper.readValue<Command>("/commands/kin_18.json".asResource())

        val httpResponse = mockk<HttpResponse>()
        coEvery { httpResponse.body<YandexResponse>() } returns YandexResponse(voiceCommand.text)
        coEvery { httpResponse.status } returns HttpStatusCode.OK

        val captureFileBody = slot<String>()
        mockkStatic(::sendRequestYandexKit)
        coEvery {
            sendRequestYandexKit(
                client = any(),
                fileBody = capture(captureFileBody),
            )
        } returns httpResponse

        testClient.post("/api/v1/command") {
            basicAuth(username = "foo", password = "bar")
            contentType(ContentType.Application.Json)
            setBody<CommandRequest>(CommandRequest(format = VoiceFormat.OGG, fileBase64 = voiceCommand.file))
        }.apply {
            assertEquals(voiceCommand.file, captureFileBody.captured)
        }
    }

    @Test
    fun `Validate response processing from Yandex API (mocked) - OK`(): Unit = runBlocking {
        val voiceCommand = objectMapper.readValue<Command>("/commands/kin_18.json".asResource())

        val httpResponse = mockk<HttpResponse>()
        coEvery { httpResponse.body<YandexResponse>() } returns YandexResponse(voiceCommand.text)
        coEvery { httpResponse.status } returns HttpStatusCode.OK

        mockkStatic(::sendRequestYandexKit)
        coEvery { sendRequestYandexKit(client = any(), fileBody = any()) } returns httpResponse

        testClient.post("/api/v1/command") {
            basicAuth(username = "foo", password = "bar")
            contentType(ContentType.Application.Json)
            setBody<CommandRequest>(CommandRequest(format = VoiceFormat.OGG, fileBase64 = voiceCommand.file))
        }.apply {
            val response = body<CommandResponse>()
            assertEquals(voiceCommand.text, response.transcription)
            assertEquals(CommandType.DICE, response.commandType)
            assertTrue(response.wasRecognized)
            assertTrue(response.commandResult.toString().toInt() in 1..18)
        }
    }

    companion object {
        private lateinit var testApp: TestApplication
        lateinit var testClient: HttpClient

        val objectMapper = jacksonObjectMapper()

        @JvmStatic
        @BeforeAll
        fun setup() {
            testApp = TestApplication {
                environment {
                    config = ApplicationConfig("application-test.conf")
                }
                application {
                    module(HttpClient().config {
                        install(ContentNegotiation) {
                            jackson()
                        }
                    })
                }
            }
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
