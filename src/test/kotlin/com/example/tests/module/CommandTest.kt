package com.example.tests.module

import com.example.adapters.AdapterType
import com.example.adapters.YandexResponse
import com.example.model.Command
import com.example.models.*
import com.example.module
import com.example.plugins.server.configureAuthentication
import com.example.plugins.server.configureLogging
import com.example.plugins.server.configureRouting
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.ktor.test.dispatcher.*
import io.ktor.utils.io.*
import org.junit.AfterClass
import org.junit.BeforeClass
import kotlin.test.*

class CommandTest {

    @Test
    fun test() = testApplication {
        val mockEngine = MockEngine { request ->
            respond(
                content = jacksonObjectMapper().writeValueAsString(YandexResponse("test")),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        environment {
            config = ApplicationConfig("application-custom.conf")
        }
        application {
            module(HttpClient(mockEngine).config {
                install(ContentNegotiation) {
                    jackson()
                }
            })
//            configureAuthentication()
//            configureLogging()
//            configureRouting(HttpClient(mockEngine),AdapterType.Yandex)
        }

        externalServices {
            hosts("https://stt.api.cloud.yandex.net") {
                install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
                    jackson()
                }
                routing {
                    post("/speech/v1/stt:recognize?topic=general") {
                        call.respond(YandexResponse("JetBrains"))
                    }
                    post {
                        call.respond(YandexResponse("JetBrains"))
                    }
                }
            }
        }

        val voiceCommand = objectMapper.readValue<Command>("/commands/kin_18.json".asResource())

        client.config {
            install(ContentNegotiation) {
                jackson {
                    setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE) // use snake_case
                    setSerializationInclusion(JsonInclude.Include.NON_NULL) // ignore NULL attributes
                }
            }
            install(Logging) {
                level = LogLevel.ALL
            }
        }.post("/api/v1/command") {
            basicAuth(username = "foo", password = "bar")
            contentType(ContentType.Application.Json)
            setBody<CommandRequest>(CommandRequest(format = VoiceFormat.OGG, fileBase64 = voiceCommand.file))
        }.apply {
//            assertEquals(HttpStatusCode.OK, status)
            val response = body<CommandResponse>()
//            assertTrue(response.wasRecognized)
//            assertEquals(CommandType.DICE, response.commandType)
//            // TODO: to check that result is an integer
//            assertTrue(response.commandResult.toString().toInt() in 0..18)
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
        lateinit var testClient: HttpClient

        val objectMapper = jacksonObjectMapper()

        @JvmStatic
        @BeforeClass
        fun setup() {
            testApp = TestApplication {
                val mockEngine = MockEngine { request ->
                    respond(
                        content = ByteReadChannel("AAAAAAAAAA!!!!!"),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }

                application {
                    configureRouting(HttpClient(mockEngine),AdapterType.Yandex)
                }

                externalServices {
//                    hosts("https://stt.api.cloud.yandex.net") {
                    hosts("https://stt.api.cloud.yandex.net") {
//                        install(io.ktor.server.plugins.contentnegotiation.ContentNegotiation) {
//                            jackson()
//                        }
                        routing {
//                            post("speech/v1/stt:recognize") {
                            post("/speech/v1/stt:recognize?topic=general") {
                                call.respond(YandexResponse("JetBrains"))
                            }
                            post {
                                call.respond(YandexResponse("JetBrains"))
                            }
                        }
                    }
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

    private fun String.asResource(): String = object {}.javaClass.getResource(this).readText()
}
