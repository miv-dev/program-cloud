package ru.dev.miv.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.dev.miv.services.UserService

fun Route.userRouting(userService: UserService) {
    route("/users"){
        get {
            call.respond(userService.users())
        }
        authenticate("access") {
            get("/current") {
                val principal = call.principal<UserIdPrincipal>()
                if (principal != null){
                    val userId = principal.name
                    userService.userById(userId).let {
                        call.respond(it)
                    }
                }else {
                    call.respond(HttpStatusCode.BadRequest,"User is null")
                }
            }
        }
    }

}