package ru.dev.miv.routing

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.dev.miv.services.ProgramService

fun Route.programRouting() {
    val service = ProgramService()
    route("/programs") {
        post("/parsing") {
            val html = call.receiveText()
            val program = service.parsing(html)

            call.respond(program)
        }

        post("/upload") {
            val multipart = call.receiveMultipart()
            multipart.forEachPart { part ->
                if (part is PartData.FileItem) {
                    service.uploadFile(part)
                }
            }
            call.respond(HttpStatusCode.OK)
        }
    }
}
