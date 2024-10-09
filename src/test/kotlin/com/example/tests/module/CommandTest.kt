package com.example.tests.module

import com.example.adapters.AiAdapterResponse
import com.example.adapters.yandex.YandexAdapter
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
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import io.mockk.*
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.*

@MockKExtension.ConfirmVerification
class CommandTest {
    private lateinit var testApp: TestApplication
    private lateinit var testClient: HttpClient
    private val objectMapper = jacksonObjectMapper()

    @Test
    fun `Validate proper call to Yandex API Speech To Text (mocked) - OK`(): Unit =
        runBlocking {
            val voiceCommand = objectMapper.readValue<Command>("/commands/kin_18.json".asResource())
            val captureFileBody = slot<String>()
            mockkConstructor(YandexAdapter::class)

            coEvery {
                anyConstructed<YandexAdapter>().transcribe(
                    fileFormat = any(),
                    fileBody = capture(captureFileBody),
                )
            } returns
                AiAdapterResponse(
                    isSuccessful = true,
                    result = voiceCommand.text,
                )

            testClient
                .post("/api/v1/command") {
                    basicAuth(username = "foo", password = "bar")
                    contentType(ContentType.Application.Json)
                    setBody<CommandRequest>(CommandRequest(format = "OGG", fileBase64 = voiceCommand.file))
                }.apply {
                    assertEquals(voiceCommand.file, captureFileBody.captured)
                }
        }

    @Test
    fun `Validate response processing from Yandex API Speech To Text (mocked) - OK`(): Unit =
        runBlocking {
            val voiceCommand = objectMapper.readValue<Command>("/commands/kin_18.json".asResource())

            mockkConstructor(YandexAdapter::class)
            coEvery {
                anyConstructed<YandexAdapter>().transcribe(
                    fileFormat = any(),
                    fileBody = any(),
                )
            } returns
                AiAdapterResponse(
                    isSuccessful = true,
                    result = voiceCommand.text,
                )

            testClient
                .post("/api/v1/command") {
                    basicAuth(username = "foo", password = "bar")
                    contentType(ContentType.Application.Json)
                    setBody<CommandRequest>(CommandRequest(format = "OGG", fileBase64 = voiceCommand.file))
                }.apply {
                    val response = body<CommandResponse>()
                    assertEquals(voiceCommand.text, response.transcription)
                    assertEquals(CommandType.DICE, response.commandType)
                    assertTrue(response.wasRecognized)
                    assertTrue(response.commandResult.toString().toInt() in 1..18)
                }
        }

    @Test
    fun `Validate proper call to Yandex API GPT (mocked) - OK`(): Unit =
        runBlocking {
            val voiceCommand = objectMapper.readValue<Command>("/commands/dai_elfa.json".asResource())
            val captureCommand = slot<String>()
            mockkConstructor(YandexAdapter::class)

            coEvery {
                anyConstructed<YandexAdapter>().transcribe(
                    fileFormat = any(),
                    fileBody = any(),
                )
            } returns
                AiAdapterResponse(
                    isSuccessful = true,
                    result = voiceCommand.text,
                )
            coEvery {
                anyConstructed<YandexAdapter>().askGpt(
                    gptCommand = capture(captureCommand),
                )
            } returns
                AiAdapterResponse(
                    isSuccessful = true,
                )

            testClient
                .post("/api/v1/command") {
                    basicAuth(username = "foo", password = "bar")
                    contentType(ContentType.Application.Json)
                    setBody<CommandRequest>(CommandRequest(format = "OGG", fileBase64 = voiceCommand.file))
                }.apply {
                    assertEquals(voiceCommand.text.splitToSequence(" ").elementAt(1), captureCommand.captured)
                }
        }

    @Test
    fun `Validate response processing from Yandex API GPT (mocked) - OK`(): Unit =
        runBlocking {
            val voiceCommand = objectMapper.readValue<Command>("/commands/dai_elfa.json".asResource())
            val expectedResult = "\"Legolas, level 10\""

            mockkConstructor(YandexAdapter::class)
            coEvery {
                anyConstructed<YandexAdapter>().transcribe(
                    fileFormat = any(),
                    fileBody = any(),
                )
            } returns
                AiAdapterResponse(
                    isSuccessful = true,
                    result = voiceCommand.text,
                )
            coEvery {
                anyConstructed<YandexAdapter>().askGpt(
                    gptCommand = any(),
                )
            } returns
                AiAdapterResponse(
                    isSuccessful = true,
                    result = expectedResult,
                )

            testClient
                .post("/api/v1/command") {
                    basicAuth(username = "foo", password = "bar")
                    contentType(ContentType.Application.Json)
                    setBody<CommandRequest>(CommandRequest(format = "OGG", fileBase64 = voiceCommand.file))
                }.apply {
                    val response = body<CommandResponseCharacter>()
                    assertEquals(voiceCommand.text, response.transcription)
                    assertEquals(CommandType.CHARACTER, response.commandType)
                    assertTrue(response.wasRecognized)
                    assertNotNull(response.commandResult)
                    if (response.commandResult != null) {
                        assertEquals(expectedResult, response.commandResult!!.description)
                        // ToDo: to evaluate more deeply
                        assertTrue(response.commandResult!!.charisma > 0)
                        assertTrue(response.commandResult!!.constitution > 0)
                        assertTrue(response.commandResult!!.dexterity > 0)
                        assertTrue(response.commandResult!!.intelligence > 0)
                        assertTrue(response.commandResult!!.level > 0)
                        assertTrue(response.commandResult!!.strength > 0)
                        assertTrue(response.commandResult!!.wisdom > 0)
                    }
                }
        }

    @BeforeEach
    fun prepare() {
        testApp =
            TestApplication {
                environment {
                    config = ApplicationConfig("application-test.yaml")
                }
                application {
                    module(
                        HttpClient().config {
                            install(ContentNegotiation) {
                                jackson()
                            }
                        },
                    )
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

    @AfterEach
    fun cleanMocks() {
        testApp.stop()
    }
}
