package dev.decagon.godday

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import dev.decagon.godday.plugins.*
import dev.decagon.godday.repository.*
import freemarker.cache.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.freemarker.*
import io.ktor.gson.*
import io.ktor.http.*
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

        // ContentNegotiation for serializing kotlin data to JSON using moshi
        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
            }
        }

        install(FreeMarker) {
            templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
        }

        val db = InMemoryRepository()

        configureRouting(db)
    }.start(wait = true)
}

const val API_VERSION = "/api/v1"