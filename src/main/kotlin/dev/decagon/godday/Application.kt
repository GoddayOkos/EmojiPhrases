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
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.sessions.*
import java.net.*
import java.util.concurrent.*

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

        // Location for typed-safe routing
        install(Locations)

        // Install Sessions for managing sign-in users
        install(Sessions) {
            cookie<EPSession>("SESSION") {
                transform(SessionTransportTransformerMessageAuthentication(hashKey))
            }
        }

        val hashFunction = { s: String -> hash(s) }

        // Initialise db
        DatabaseFactory.init()

        val db = EmojiPhrasesRepository()


        // Call to the function hosting all the routes
        configureRouting(db, hashFunction)
    }.start(wait = true)
}

const val API_VERSION = "/api/v1"

suspend fun ApplicationCall.redirect(location: Any) {
    respondRedirect(application.locations.href(location))
}

fun ApplicationCall.refererHost() = request.header(HttpHeaders.Referrer)?.let { URI.create(it).host }

fun ApplicationCall.securityCode(date: Long, user: User, hashFunction: (String) -> String) =
    hashFunction("$date:${user.userId}:${request.host()}:${refererHost()}")

fun ApplicationCall.verifyCode(date: Long, user: User, code: String, hashFunction: (String) -> String) =
    securityCode(date, user, hashFunction) == code &&
            (System.currentTimeMillis() - date).let { it > 0 && it < TimeUnit.MILLISECONDS.convert(2, TimeUnit.HOURS) }