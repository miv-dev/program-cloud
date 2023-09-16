package ru.dev.miv

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import ru.dev.miv.routing.programRouting


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }

    routing {
        programRouting()
    }
}
