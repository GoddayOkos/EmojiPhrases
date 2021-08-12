package dev.decagon.godday

import dev.decagon.godday.model.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import dev.decagon.godday.plugins.*
import dev.decagon.godday.repository.*
import freemarker.cache.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
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
    embeddedServer(Netty, port = System.getenv("PORT").toInt()) {
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

        // Create Repository instance
        val db = EmojiPhrasesRepository()

        // Create JWTService instance
        val jwtService = JwtService()

        // Install Authentication
        install(Authentication) {
            jwt("jwt") {
                verifier(jwtService.verifier)
                realm = "emojiphrases app"
                validate {
                    val payload = it.payload
                    val claim = payload.getClaim("id")
                    val claimString = claim.asString()
                    val user = db.userById(claimString)
                    user
                }
            }
        }

        // Call to the function hosting all the routes
        configureRouting(db, hashFunction, jwtService)
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

val ApplicationCall.apiUser get() = authentication.principal<User>()