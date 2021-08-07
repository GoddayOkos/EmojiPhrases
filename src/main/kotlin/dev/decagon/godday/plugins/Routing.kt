package dev.decagon.godday.plugins

import dev.decagon.godday.api.*
import dev.decagon.godday.model.*
import dev.decagon.godday.repository.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.request.*

fun Application.configureRouting(db: Repository) {
    // Starting point for a Ktor app:
    routing {
        static("/static") {
            resource("images")
        }

        get("/") {
            call.respond(FreeMarkerContent("home.ftl", null))
        }

        get("/hello") {
            call.respondText("hello, ktor")
        }

        get("/about") {
            call.respondText("About")
        }

        // API
        phrase(db)   // POST
        phrases(db)  // GET
    }

}
