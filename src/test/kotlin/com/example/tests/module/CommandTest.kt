package com.example.tests.module

import com.example.adapters.YandexResponse
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
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import io.ktor.test.dispatcher.*
import org.junit.AfterClass
import org.junit.BeforeClass
import kotlin.test.*

private fun MockRequestHandleScope.handleCommandRequest(request: HttpRequestData): HttpResponseData? =
    if (request.url.toString() == "https://stt.api.cloud.yandex.net/speech/v1/stt:recognize?topic=general") {
        respond(
            content = jacksonObjectMapper().writeValueAsString(YandexResponse("Кинь 18")),
            status = HttpStatusCode.OK,
            headers = headersOf(HttpHeaders.ContentType, "application/json"),
        )
    } else {
        null
    }

private fun MockRequestHandleScope.errorResponse(): HttpResponseData {
    return respond(
        content = jacksonObjectMapper().writeValueAsString(YandexResponse("")),
        status = HttpStatusCode.BadRequest,
        headers = headersOf(HttpHeaders.ContentType, "application/json"),
    )
}

class CommandTest {
    @Test
    fun `Throw dice up to 18 with correct command (no AI connection)`() =
        testSuspend {
            val voiceCommand = objectMapper.readValue<Command>("/commands/kin_18.json".asResource())

            testClient.post("/api/v1/command") {
                basicAuth(username = "foo", password = "bar")
                contentType(ContentType.Application.Json)
                setBody<CommandRequest>(CommandRequest(format = VoiceFormat.OGG, fileBase64 = voiceCommand.file))
            }.apply {
                assertEquals(HttpStatusCode.OK, status)
                val response = body<CommandResponse>()
                assertTrue(response.wasRecognized)
                assertEquals(CommandType.DICE, response.commandType)
                // TODO: to check that result is an integer
                assertTrue(response.commandResult.toString().toInt() in 0..18)
                assertEquals(voiceCommand.text, response.transcription)
            }
        }

    companion object {
        private lateinit var testApp: TestApplication
        private lateinit var mockEngine: MockEngine
        lateinit var testClient: HttpClient

        val objectMapper = jacksonObjectMapper()

        @JvmStatic
        @BeforeClass
        fun setup() {
            mockEngine = MockEngine { request ->
                handleCommandRequest(request)
                    ?: errorResponse()
            }

            testApp = TestApplication {
                environment {
                    config = ApplicationConfig("application-test.conf")
                }
                application {
                    module(HttpClient(mockEngine).config {
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
        @AfterClass
        fun teardown() {
            testApp.stop()
        }
    }
}
