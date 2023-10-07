package ru.dev.miv.services

import io.ktor.http.content.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import ru.dev.miv.db.entities.FileEntity
import ru.dev.miv.db.entities.PartEntity
import ru.dev.miv.db.entities.ProgramEntity
import ru.dev.miv.db.entities.ProgramFilesEntity
import ru.dev.miv.models.ProgramModel
import ru.dev.miv.models.ProgramParsed
import ru.dev.miv.response_models.UploadResponse
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.collections.set



class ProgramService(
    private val uploadPath: String = "data/files"
) {
    fun parsing(html: String): ProgramParsed = ParsingService().parseHtml(html)


    private fun uploadFile(data: PartData, path: String, filename: String) {
        if (data is PartData.FileItem) {
            Files.createDirectories(Paths.get(path))
            data.streamProvider().readBytes().also {
                File("$path/$filename").writeBytes(it)
            }
        } else throw RuntimeException("Not Valid Data")
    }

    suspend fun programs(): List<ProgramModel> = newSuspendedTransaction {
        ProgramEntity.all().map { it.toModel() }
    }


    suspend fun addProgram(data: MultiPartData): UploadResponse {
        val uploadedFiles = mutableMapOf<String, FileEntity>()
        val errorList = mutableListOf<String>()
        val uuid =
            data.readAllParts().associateBy { it.name }.takeIf { it.keys.containsAll(REQUIRED_PART_DATA) }?.let { map ->
                val id = UUID.randomUUID()
                val path = "$uploadPath/programs/$id"

                val program = map["program"]?.let {
                    if (it is PartData.FormItem) {
                        return@let Json.decodeFromString<ProgramParsed>(it.value)
                    } else {
                        throw RuntimeException("Program info is not valid")
                    }
                } ?: throw RuntimeException("Program didn't pass")


                map.forEach { (key, value) ->
                    key?.let {
                        try {
                            when (key) {
                                "tmt" -> TMT_FILENAME

                                "preview" -> PREVIEW_FILENAME

                                "lst" -> LST_FILENAME

                                else -> null
                            }?.let { filename ->
                                uploadFile(
                                    value, path, filename
                                )
                                newSuspendedTransaction {
                                    uploadedFiles[key] = FileEntity.new {
                                        this.path = "$id/$filename"
                                    }

                                }

                            }
                        } catch (e: RuntimeException) {

                            errorList.add(key)

                        }
                    }

                }

                newSuspendedTransaction {
                    val files = ProgramFilesEntity.new {
                        lstFile = uploadedFiles["lst"]
                        previewFile = uploadedFiles["preview"]
                        tmtFile = uploadedFiles["tmt"]

                    }

                    val partEntityList = program.parts.map {
                        PartEntity.new {
                            this.dimensions = Json.encodeToString(it.dimensions)
                            this.quantity = it.quantity
                            this.geoFilename = it.geoFilename
                        }
                    }


                    ProgramEntity.new(id) {
                        this.programId = program.programId
                        this.name = program.name
                        this.machiningTime = program.machiningTime
                        this.blank = Json.encodeToString(program.blank)
                        this.files = files
                        this.tools = Json.encodeToString(program.tools)
                        this.parts = SizedCollection(partEntityList)
                    }


                }

                id

            } ?: throw RuntimeException("Not all data passed")

        return UploadResponse(
            programId = uuid, uploadedFiles.keys.toList(), errorList
        )
    }

    companion object {

        const val TMT_FILENAME = "tmt.TMT"
        const val PREVIEW_FILENAME = "preview.BMP"
        const val LST_FILENAME = "lst.LST"


        val REQUIRED_PART_DATA = listOf("program", "lst", "preview", "tmt")
    }
}

