package dev.decagon.godday.plugins

import dev.decagon.godday.repository.*
import io.ktor.application.*
import io.ktor.freemarker.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

@Location("/signup")
data class Signup(
    val userId: String = "",
    val displayName: String = "",
    val email: String = "",
    val error: String = ""
)

fun Route.signup(db: Repository, hashFunction: (String) -> String) {
    get<Signup> {
        call.respond(FreeMarkerContent("signup.ftl", null))
    }
}