package ru.dev.miv.plugins

import io.ktor.server.application.*
import ru.dev.miv.db.DBConfig
import ru.dev.miv.utils.extensions.stringProperty

fun Application.db(){
    val url = stringProperty("db.url")
    val username = stringProperty("db.username")
    val password = stringProperty("db.password")

    val credentials = DBConfig.Credentials(url, username, password)

    DBConfig.setup(credentials)
}