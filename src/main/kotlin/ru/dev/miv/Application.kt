package ru.dev.miv

import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import ru.dev.miv.db.DBConfig
import ru.dev.miv.routing.programRouting
import java.io.File
import io.ktor.server.http.content.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import ru.dev.miv.routing.authRouting
import ru.dev.miv.routing.userRouting
import ru.dev.miv.services.TokenService
import ru.dev.miv.services.UserService


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

    install(CORS) {
        allowHost("*")
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)

    }
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
    authenticationPlugin {
        val userService = UserService()
        staticFiles("/static", File("data/files"))
        programRouting()
        userRouting(userService)
        authRouting(userService, it)
    }


}

fun Application.longProperty(path: String): Long =
    stringProperty(path).toLong()

fun Application.authenticationPlugin(routing: Route.(tokenService: TokenService) -> Unit) {
    val tokenService = TokenService(
        stringProperty("jwt.issuer"),
        Algorithm.HMAC256(stringProperty("jwt.access.secret")),
        longProperty("jwt.access.lifetime"),
        longProperty("jwt.refresh.lifetime")
    )


    install(Authentication) {
        jwt("access") {
            verifier {
                tokenService.makeJWTVerifier()
            }
            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
            validate { token ->
                if (token.payload.expiresAt.time > System.currentTimeMillis())
                    UserIdPrincipal(name = token.payload.getClaim("userId").asString())
                else null
            }
        }

    }

    routing {
        routing(tokenService)
    }
}