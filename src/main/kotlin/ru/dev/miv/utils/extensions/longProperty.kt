package ru.dev.miv.utils.extensions

import io.ktor.server.application.*

fun Application.longProperty(path: String): Long =
    stringProperty(path).toLong()

