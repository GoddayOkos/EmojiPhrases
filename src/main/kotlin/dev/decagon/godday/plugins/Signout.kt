package dev.decagon.godday.plugins

import dev.decagon.godday.*
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.routing.*

@Location("/signout")
class Signout

fun Route.signout() {
    get<Signout> {
        call.redirect(Signin())
    }
}