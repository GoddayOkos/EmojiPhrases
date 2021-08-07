package dev.decagon.godday.model

import io.ktor.auth.*

data class User(val displayName: String) : Principal