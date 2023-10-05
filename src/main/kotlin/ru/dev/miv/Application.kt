package ru.dev.miv

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.flywaydb.core.Flyway
import ru.dev.miv.db.DBConfig
import ru.dev.miv.routing.programRouting


fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}
fun Application.stringProperty(path: String): String =
    this.environment.config.property(path).getString()
fun Application.module() {

    val url = stringProperty("db.url")
    val username = stringProperty("db.username")
    val password = stringProperty("db.password")

    val credentials = DBConfig.Credentials(url, username, password)

    DBConfig.setup(credentials)

    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }


    routing {
        programRouting()
    }
}
