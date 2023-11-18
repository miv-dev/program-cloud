package ru.dev.miv.routing

import dev.miv.models.RefreshToken
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.dev.miv.request_models.LoginRequest
import ru.dev.miv.services.TokenService
import ru.dev.miv.services.UserService

@kotlinx.serialization.Serializable
data class SuccessResponse<T>(
    val success: Boolean,
    val data: T
)

@kotlinx.serialization.Serializable
data class FailResponse(
    val success: Boolean,
    val error: String
)

fun Route.authRouting(userService: UserService, tokenService: TokenService) {
    route("/auth") {
        post("/refresh") {
            val oldRT = call.receive<RefreshToken>().refreshToken // old refresh token

            runCatching {
                tokenService.updateByRefreshToken(oldRT)
            }.onSuccess {
                call.respond(
                    SuccessResponse(
                        success = true,
                        data = it
                    )
                )
            }.onFailure {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    FailResponse(
                        success = false,
                        error = "invalid token"
                    )
                )
            }


        }

        post("/login") {
            val authUser = call.receive<LoginRequest>()
            runCatching {
                userService.login(authUser.email, authUser.password)
            }
                .onSuccess { user ->
                    val tokenPair = tokenService.generateTokenPair(user.uuid!!)
                    call.respond(
                        SuccessResponse(
                            true,
                            tokenPair
                        )
                    )
                }
                .onFailure {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        FailResponse(
                            false,
                            it.localizedMessage
                        )
                    )
                }


        }
    }
}
