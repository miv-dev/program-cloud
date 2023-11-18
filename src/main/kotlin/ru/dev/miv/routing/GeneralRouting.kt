package ru.dev.miv.routing

import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import ru.dev.miv.services.TokenService
import ru.dev.miv.services.UserService
import java.io.File

fun Route.generalRouting(tokenService: TokenService) {
    val userService = UserService()
    staticFiles("/static", File("data/files"))
    programRouting()
    userRouting(userService)
    authRouting(userService, tokenService)

}