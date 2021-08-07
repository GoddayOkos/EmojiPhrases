package dev.decagon.godday

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import dev.decagon.godday.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting()
    }.start(wait = true)
}

const val API_VERSION = "/api/v1"