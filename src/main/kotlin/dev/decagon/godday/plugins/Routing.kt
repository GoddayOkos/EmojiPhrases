package dev.decagon.godday.plugins

import dev.decagon.godday.api.*
import dev.decagon.godday.repository.*
import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.response.*


@Location("/")
class Home

@Location("/about")
class About

fun Application.configureRouting(db: Repository, hashFunction: (String) -> String) {
    // Starting point for a Ktor app:
    routing {
        static("/static") {
            resources("images")
        }

        get<Home> {
            call.respond(FreeMarkerContent("home.ftl", null))
        }

        get("/hello") {
            call.respondText("hello, ktor")
        }

        get<About> {
            call.respond(FreeMarkerContent("about.ftl", null))
        }

        // Auth Routes
        signin(db, hashFunction)
        signout()
        signup(db, hashFunction)

        // API
        phrase(db)   // POST
        phrases(db)  // GET
    }

}
