package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import org.slf4j.event.Level

fun Application.configureLogging() {
    install(CallLogging) {
        // TODO: should be based on logback config
        level = Level.TRACE
    }
}