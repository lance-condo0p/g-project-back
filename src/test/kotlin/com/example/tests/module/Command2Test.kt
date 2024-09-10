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
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import io.mockk.*
import kotlinx.coroutines.runBlocking
import kotlin.test.*

class Command2Test {
    @Test
    fun `Validate request to AI (mocked)`() {
        val objectMapper = jacksonObjectMapper()

        val mockedClient = mockk<HttpClient>(relaxed = true)
        val httpResponse = mockk<HttpResponse>()

        mockkConstructor(HttpStatement::class)
        coEvery { anyConstructed<HttpStatement>().execute() } returns mockk {
            coEvery { status } returns HttpStatusCode.OK
            coEvery { body<YandexResponse>() } returns YandexResponse("ignored")
            coEvery { body<CommandResponse>() } returns CommandResponse(true)
        }

        coEvery { httpResponse.body<YandexResponse>() } returns YandexResponse("ignored")
        coEvery { httpResponse.body<CommandResponse>() } returns CommandResponse(true, "Кинь 19")
        coEvery { mockedClient.request() } returns httpResponse

        val testApp = TestApplication {
            environment {
                config = ApplicationConfig("application-test.yaml")
            }
            application {
                module(mockedClient)
            }
        }
        val testClient: HttpClient = testApp.createClient {
            install(ContentNegotiation) {
                jackson {
                    setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE) // use snake_case
                    setSerializationInclusion(JsonInclude.Include.NON_NULL) // ignore NULL attributes
                }
            }
        }

//        testSuspend {
        runBlocking {
            val voiceCommand = objectMapper.readValue<Command>("/commands/kin_18.json".asResource())

            testClient.post("/api/v1/command") {
                basicAuth(username = "foo", password = "bar")
                contentType(ContentType.Application.Json)
                setBody<CommandRequest>(CommandRequest(format = VoiceFormat.OGG, fileBase64 = voiceCommand.file))
            }.apply {
                val response = body<CommandResponse>()
                assertEquals(voiceCommand.text, response.transcription)
            }
        }
    }
}
