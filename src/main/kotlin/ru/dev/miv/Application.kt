package ru.dev.miv

import io.ktor.server.application.*
import ru.dev.miv.plugins.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

}
