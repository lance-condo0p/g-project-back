package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.Ignore
import kotlin.test.*

class ApplicationTest {
    @Test
//    @Ignore
    fun `Check GET without parameters returns Empty`() = testApplication {
        val response = client.get("/my_request")
        assertEquals(
            """Empty!""",
            response.bodyAsText()
        )
        assertEquals(HttpStatusCode.OK, response.status)
    }
}