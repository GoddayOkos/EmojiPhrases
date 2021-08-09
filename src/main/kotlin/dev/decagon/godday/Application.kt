package dev.decagon.godday

import dev.decagon.godday.model.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import dev.decagon.godday.plugins.*
import dev.decagon.godday.repository.*
import freemarker.cache.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.freemarker.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {

        // DefaultHeaders are used to set HTTP Headers for responses
        install(DefaultHeaders)

        // Used to respond to failures or errors
        install(StatusPages) {
            exception<Throwable> { e ->
                call.respondText(e.localizedMessage,
                 ContentType.Text.Plain, HttpStatusCode.InternalServerError
                )
            }
        }

        // ContentNegotiation for serializing kotlin data to JSON using gson
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
            }
        }

        // FreeMarker template for writing frontend HTML codes
        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        }

        // Authentication for signing in users with credentials
        install(Authentication) {
            basic(name = "auth") {
               realm = "Ktor server"
               validate { credentials ->
                   if (credentials.password == "${credentials.name}123") User(credentials.name) else null
               }
            }
        }

        // Location for typed-safe routing
        install(Locations)

        // Initialise db
        DatabaseFactory.init()

        val db = EmojiPhrasesRepository()

        // Call to the function hosting all the routes
        configureRouting(db)
    }.start(wait = true)
}

const val API_VERSION = "/api/v1"

suspend fun ApplicationCall.redirect(location: Any) {
    respondRedirect(application.locations.href(location))
}