package ru.dev.miv.routing

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.dev.miv.response_models.ParsingRequest
import ru.dev.miv.services.ProgramService


fun Route.programRouting() {
    val service = ProgramService()
    route("/programs") {


        post("/parsing") {
            val parsingRequest = call.receive<ParsingRequest>()
            runCatching {
                service.parsing(parsingRequest)
            }.onSuccess {
                call.respond(it)
            }.onFailure {
                call.respond(HttpStatusCode.BadRequest, hashMapOf("error" to it.localizedMessage))
            }


        }


        post {
            val searchText = call.receiveText()
            val programs = if (searchText == "") {
                service.programs()
            } else {
                service.findProgram(searchText)
            }

            call.respond(programs)
        }
        get {
            val programs = service.programs()
            call.respond(programs)
        }
        authenticate("access") {


            post("/upload") {
                val multipart = call.receiveMultipart()
                runCatching {
                    service.addProgram(multipart)
                }.onSuccess {
                    call.respond(HttpStatusCode.OK, it)
                }.onFailure {
                    call.respond(HttpStatusCode.BadRequest, hashMapOf("error" to it.localizedMessage))
                }


            }
        }
    }
}
