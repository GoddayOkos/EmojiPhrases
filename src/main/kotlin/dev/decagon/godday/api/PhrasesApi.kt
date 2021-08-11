package dev.decagon.godday.api

import dev.decagon.godday.*
import dev.decagon.godday.model.*
import dev.decagon.godday.plugins.*
import dev.decagon.godday.repository.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.freemarker.*
import io.ktor.locations.*
import io.ktor.locations.post
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*


import kotlin.IllegalArgumentException

const val PHRASE_API_ENDPOINT = "$API_VERSION/phrase"
const val PHRASES = "/phrases"

@Location(PHRASES)
class Phrases

@Location(PHRASE_API_ENDPOINT)
class PhrasesApi

//fun Route.phrase(db: Repository) {
//    post(PHRASE_ENDPOINT) {
//        val request = call.receive<Request>()
//        val phrase = db.add("", request.emoji, request.phrase)
//        call.respond(phrase)
//    }
//}

fun Route.phrasesApi(db: Repository) {
    authenticate("jwt") {
        get<PhrasesApi> {
            call.respond(db.phrases())
        }
    }
}

fun Route.phrases(db: Repository, hashFunction: (String) -> String) {

    get<Phrases> {
        val user = call.sessions.get<EPSession>()?.let { db.user(it.userId) }

        if (user == null) {
            call.redirect(Signin())
        } else {
            val phrases = db.phrases()
            val date = System.currentTimeMillis()
            val code = call.securityCode(date, user, hashFunction)
            call.respond(
                FreeMarkerContent(
                    "phrases.ftl", mapOf(
                        "phrases" to phrases, "user" to user, "date" to date, "code" to code
                    ), user.userId
                )
            )
        }
    }

    post<Phrases> {
        val user = call.sessions.get<EPSession>()?.let { db.user(it.userId) }

        val params = call.receiveParameters()
        val date = params["date"]?.toLongOrNull() ?: return@post call.redirect(it)
        val code = params["code"] ?: return@post call.redirect(it)
        val action = params["action"] ?: throw IllegalArgumentException("Missing parater: action")

        if (user == null || !call.verifyCode(date, user, code, hashFunction)) {
            call.redirect(Signin())
        }

        when (action) {
            "delete" -> {
                val id = params["id"] ?: throw IllegalArgumentException("Missing parameter: id")
                db.remove(id)
            }
            "add" -> {
                val emoji = params["emoji"] ?: throw IllegalArgumentException("Missing parameter: emoji")
                val phrase = params["phrase"] ?: throw IllegalArgumentException("Missing parameter: phrase")
                db.add(user!!.userId, emoji, phrase)
            }
        }
        call.redirect(Phrases())
    }
}