package dev.decagon.godday.plugins

import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*

fun Application.configureRouting() {
    // Starting point for a Ktor app:
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/hello") {
            call.respondText("hello, ktor")
        }

        get("/about") {
            call.respondText("About")
        }
    }

}
