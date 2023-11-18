package ru.dev.miv

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import ru.dev.miv.routing.programRouting
import java.io.File
import io.ktor.server.http.content.*
import ru.dev.miv.plugins.authenticationPlugin
import ru.dev.miv.plugins.cors
import ru.dev.miv.plugins.db
import ru.dev.miv.routing.authRouting
import ru.dev.miv.routing.generalRouting
import ru.dev.miv.routing.userRouting
import ru.dev.miv.services.UserService


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    db()
    cors()

    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
    authenticationPlugin {
        generalRouting(it)
    }


}

