package ru.dev.miv.plugins

import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.dev.miv.services.TokenService
import ru.dev.miv.utils.extensions.longProperty
import ru.dev.miv.utils.extensions.stringProperty

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