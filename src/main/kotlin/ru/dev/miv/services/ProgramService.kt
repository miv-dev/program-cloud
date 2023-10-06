package ru.dev.miv.services

import io.ktor.http.content.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import ru.dev.miv.db.entities.*
import ru.dev.miv.models.Blank
import ru.dev.miv.models.Part
import ru.dev.miv.models.ProgramParsed
import ru.dev.miv.response_models.UploadResponse
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.collections.set


const val filesPath = "data/files"

class ProgramService {
    fun parsing(html: String): ProgramParsed = parseHtml(html)


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
                val path = "$filesPath/programs/$id"

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
                            this.quantity = it.number
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

fun parseHtml(html: String): ProgramParsed {
    val doc = Jsoup.parse(html)
    val tables = doc.getElementsByTag("tbody")

    val parts = parseParts(tables[4].children())
    val tools = parseTools(tables[3].children())

    val preview = tables[5].getElementsByTag("img").first()?.attr("src") ?: "Undefined"

    val dict = mutableMapOf<String, String>()
    val info = tables[1].children()
    info.filter { item -> item.childrenSize() > 1 }.forEach {
        val key = it.getElementsByTag("font")[0].text()
        val value = it.getElementsByTag("font")[1].text()

        dict[key.lowercase().removeSuffix(":").replace(" ", "_")] = value

    }

    val groups = dict["program_name"]?.let {
        val regex = "([0-9]+) \\((.*)\\)".toRegex()
        val groups = regex.find(it)?.groups

        groups
    }
    val blank = dict["blank"]?.removeSuffix("mm")?.let {
        val values = it.split(" x ")

        Blank(
            values[0].toDouble(), values[1].toDouble(), values[2].toDouble()
        )
    } ?: Blank(
        -1.0,
        -1.0,
        -1.0,
    )
    val time = dict["machining_time"]?.removeSuffix(" [h:min:s]")?.let { str ->
        var count = 0
        str.split(" : ").map { it.toInt() }.also {
            count += it[0] * 3600
            count += it[1] * 60
            count += it[2]
        }
        count
    } ?: -1
    return ProgramParsed(
        programId = groups?.get(1)?.value ?: "Undefined",
        name = groups?.get(2)?.value ?: "Undefined",
        programName = dict["nc-program_name"] ?: "Undefined",
        blank = blank,
        machiningTime = time,
        preview = preview,
        tools = tools,
        parts = parts
    )
}

fun parseTools(elements: Elements): List<String> {
    val tools = mutableListOf<Map<String, String>>()

    elements.filter { item -> item.childrenSize() > 1 }.also { list ->
        val keys = mutableListOf<String>()
        list[0].getElementsByTag("b").forEach {
            keys.add(it.text().lowercase().replace(" ", "_"))
        }

        list.slice(1 until list.size).forEach {
            val tool = mutableMapOf<String, String>()
            it.getElementsByTag("font").forEachIndexed { index, element ->
                tool[keys[index]] = element.text()
            }
            tools.add(tool)
        }

    }
    return tools.map { it["remark"] ?: "Undefined" }
}

fun parseParts(elements: Elements): List<Part> {
    val parts = mutableListOf<List<Element>>()

    elements.filter { field ->
        field.childrenSize() > 1
    }.apply {
        for (i in 0 until size / 10) {
            parts.add(slice(10 * i until 10 * (i + 1)))
        }
    }

    return parts.map { parsePart(it) }
}

fun parsePart(elements: List<Element>): Part {
    val dict = mutableMapOf<String, String>()
    elements[0].also {
        val img = it.getElementsByTag("img")
        val key = it.getElementsByTag("font")[1].text()
        val value = it.getElementsByTag("font")[2].text()
        dict["preview"] = img.attr("src")
        dict[key.lowercase().removeSuffix(":").replace(" ", "_")] = value
    }
    elements.filter { it.childrenSize() < 3 }.forEach {
        val key = it.getElementsByTag("font")[0].text()
        val value = it.getElementsByTag("font")[1].text()

        dict[key.lowercase().removeSuffix(":").replace(" ", "_")] = value
    }
    val dimensions: Blank = dict["dimensions"]?.let {
        val sizes = it.removeSuffix("mm").trim().split(" x ")
        Blank(
            sizes[0].toDouble(),
            sizes[1].toDouble(),
        )
    } ?: Blank(-1.0, -1.0)


    return Part(
        number = dict["part_number"]?.toInt() ?: -1,
        dimensions = dimensions,
        geoFilename = dict["geofile_name"] ?: "Undefined",
    )
}
