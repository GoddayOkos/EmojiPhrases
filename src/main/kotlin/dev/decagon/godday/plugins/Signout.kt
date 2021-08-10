package dev.decagon.godday.plugins

import dev.decagon.godday.*
import dev.decagon.godday.model.*
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.routing.*
import io.ktor.sessions.*

@Location("/signout")
class Signout

fun Route.signout() {
    get<Signout> {
        call.sessions.clear<EPSession>()
        call.redirect(Signin())
    }
}