package dev.decagon.godday.api

import dev.decagon.godday.*
import dev.decagon.godday.api.response.*
import dev.decagon.godday.repository.*
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.locations.post
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*


@Location("/login")
class Login

fun Route.login(db: Repository, jwtService: JwtService) {
    post<Login> {
        val params = call.receive<Parameters>()
        val userId = params["userId"] ?: return@post call.redirect(it)
        val password = params["password"] ?: return@post call.redirect(it)

        val user = db.user(userId, hash(password))

        if (user != null) {
            val token = jwtService.generateToken(user)
            call.respond(LoginResponse(token))
        } else {
            call.respond(HttpStatusCode.BadRequest, "Invalid user")
        }
    }
}