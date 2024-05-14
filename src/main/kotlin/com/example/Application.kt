package com.example

import com.example.models.MyRequest
import com.example.plugins.*
import io.ktor.server.application.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*

suspend fun main(args: Array<String>): Unit {
    // client side
    val client = HttpClient(CIO) {
        install(Logging) {
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            jackson()
        }
//        engine {
//            // this: CIOEngineConfig
//            maxConnectionsCount = 1000
//            endpoint {
//                // this: EndpointConfig
//                maxConnectionsPerRoute = 100
//                pipelineMaxSize = 20
//                keepAliveTime = 5000
//                connectTimeout = 5000
//                connectAttempts = 5
//            }
////            https {
////                // this: TLSConfigBuilder
////                serverName = "api.ktor.io"
////                cipherSuites = CIOCipherSuites.SupportedSuites
////                trustManager = myCustomTrustManager
////                random = mySecureRandom
////                addKeyStore(myKeyStore, myKeyStorePassword)
////            }
//        }
    }

//    val response: HttpResponse = client.get("https://ktor.io/") {
//        contentType(ContentType.Application.Json)
//        setBody(MyRequest("3", "Jet"))
//    }

    val response: HttpResponse = client.request {
        method = HttpMethod.Get
        url {
            protocol = URLProtocol.HTTPS
            host = "ktor.io"
            path("test")
            parameters.append("myparm","1234")
        }
        contentType(ContentType.Application.Json)
        setBody(MyRequest("3", "Jet"))
    }

    println(response.status)
    client.close()
    // server side
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureRouting()
    configureSerialization()
}