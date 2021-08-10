package dev.decagon.godday.plugins

import dev.decagon.godday.repository.*
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*


@Location("/signin")
data class Signin(val userId: String = "", val error: String = "")

fun Route.signin(db: Repository, hashFunction: (String) -> String) {
    get<Signin> {
        call.respond(FreeMarkerContent("signin.ftl", null))
    }
}