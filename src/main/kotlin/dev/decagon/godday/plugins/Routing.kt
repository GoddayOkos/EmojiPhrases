package dev.decagon.godday.plugins

import dev.decagon.godday.api.*
import dev.decagon.godday.model.*
import dev.decagon.godday.repository.*
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

        fun phrase(db: Repository) {
            post(PHRASE_ENDPOINT) {
                val request = call.receive<Request>()
                val phrase = db.add(EmojiPhrase(request.emoji, request.phrase))
                call.respond(phrase)
            }
        }
    }

}
