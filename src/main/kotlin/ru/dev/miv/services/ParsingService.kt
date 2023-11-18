package ru.dev.miv.services

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import ru.dev.miv.models.*
import ru.dev.miv.response_models.ParsingRequest
import java.io.File
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.pathString

class ParsingService {

    fun parseHtml(request: ParsingRequest): ProgramModel {
        val doc = Jsoup.parse(request.program)
        val tables = doc.getElementsByTag("tbody")

        val parts = parseParts(tables[4].children())
        val tools = parseTools(tables[3].children())

        val preview = tables[5].getElementsByTag("img").first()?.attr("src") ?: STRING_UNDEFINED

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

            BlankModel(
                values[0].toDouble(), values[1].toDouble(), values[2].toDouble()
            )
        } ?: BlankModel(
            DOUBLE_UNDEFINED,
            DOUBLE_UNDEFINED,
            DOUBLE_UNDEFINED,
        )
        val time = dict["machining_time"]?.removeSuffix(" [h:min:s]")?.let { str ->
            var count = 0
            str.split(" : ").map { it.toInt() }.also {
                count += it[0] * 3600
                count += it[1] * 60
                count += it[2]
            }
            count
        } ?: INT_UNDEFINED


        val files = request.path.let {
            val html = it
            val filename = html.split("\\").last()
            val path = html.removeSuffix(filename)
            val tmt = "$path${filename.replace("HTML", "TMT")}"
            val lst = "$path${filename.replace("HTML", "LST")}"

            ProgramFilesModel(
                lst = FileModel(url = lst),
                tmt = FileModel(url = tmt),
                preview = FileModel(url = "$path$preview"),
            )
        }



        return ProgramModel(
            id = UUID.randomUUID(),
            programId = groups?.get(1)?.value ?: STRING_UNDEFINED,
            name = groups?.get(2)?.value ?: STRING_UNDEFINED,
            blank = blank,
            machiningTime = time,
            tools = tools,
            parts = parts,
            files = files
        )
    }

    private fun parseTools(elements: Elements): List<String> {
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
        return tools.map { it["remark"] ?: STRING_UNDEFINED }
    }

    private fun parseParts(elements: Elements): List<PartModel> {
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

    private fun parsePart(elements: List<Element>): PartModel {
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
        val dimensions: BlankModel = dict["dimensions"]?.let {
            val sizes = it.removeSuffix("mm").trim().split(" x ")
            BlankModel(
                sizes[0].toDouble(),
                sizes[1].toDouble(),
            )
        } ?: BlankModel(DOUBLE_UNDEFINED, DOUBLE_UNDEFINED)


        return PartModel(
            quantity = dict["part_number"]?.toInt() ?: INT_UNDEFINED,
            dimensions = dimensions,
            geoFilename = dict["geofile_name"]?.split("\\")?.last()?.removeSuffix(".GEO") ?: STRING_UNDEFINED,
        )
    }

    companion object {
        const val STRING_UNDEFINED = "Undefined"
        const val INT_UNDEFINED = -1
        const val DOUBLE_UNDEFINED = -1.0
    }

}
