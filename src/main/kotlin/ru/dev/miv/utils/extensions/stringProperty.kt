package ru.dev.miv.utils.extensions

import io.ktor.server.application.*

fun Application.stringProperty(path: String): String =
    this.environment.config.property(path).getString()