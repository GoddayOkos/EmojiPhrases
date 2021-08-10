package dev.decagon.godday.plugins

import dev.decagon.godday.api.*
import dev.decagon.godday.model.*
import dev.decagon.godday.repository.*
import io.ktor.routing.*
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.http.content.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.sessions.*


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
            val user = call.sessions.get<EPSession>()?.let { db.user(it.userId) }
            call.respond(FreeMarkerContent("home.ftl", mapOf("user" to user)))
        }

        get("/hello") {
            call.respondText("hello, ktor")
        }

        get<About> {
            val user = call.sessions.get<EPSession>()?.let { db.user(it.userId) }
            call.respond(FreeMarkerContent("about.ftl", mapOf("user" to user)))
        }

        // Auth Routes
        signin(db, hashFunction)
        signout()
        signup(db, hashFunction)

        // API
        phrase(db)   // POST
        phrases(db, hashFunction)  // GET
    }

}
