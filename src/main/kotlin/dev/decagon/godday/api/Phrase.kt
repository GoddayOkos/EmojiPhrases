package dev.decagon.godday.api

import dev.decagon.godday.*
import dev.decagon.godday.model.*
import dev.decagon.godday.repository.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.freemarker.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

const val PHRASE_ENDPOINT = "$API_VERSION/phrase"
const val PHRASES = "/phrases"

fun Route.phrase(db: Repository) {
    authenticate("auth") {
        post(PHRASE_ENDPOINT) {
            val request = call.receive<Request>()
            val phrase = db.add(EmojiPhrase(request.emoji, request.phrase))
            call.respond(phrase)
        }
    }
}

fun Route.phrases(db: Repository) {
    authenticate("auth") {
        get(PHRASES) {
            val user = call.authentication.principal as User
            val phrases = db.phrases()
            call.respond(FreeMarkerContent("phrases.ftl", mapOf("phrases" to phrases,
                "displayName" to user.displayName)))
        }
    }
}