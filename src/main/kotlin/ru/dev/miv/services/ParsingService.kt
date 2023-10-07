package ru.dev.miv.services

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import ru.dev.miv.models.Blank
import ru.dev.miv.models.Part
import ru.dev.miv.models.ProgramParsed

class ParsingService {

    fun parseHtml(html: String): ProgramParsed {
        val doc = Jsoup.parse(html)
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

            Blank(
                values[0].toDouble(), values[1].toDouble(), values[2].toDouble()
            )
        } ?: Blank(
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

        return ProgramParsed(
            programId = groups?.get(1)?.value ?: STRING_UNDEFINED,
            name = groups?.get(2)?.value ?: STRING_UNDEFINED,
            programName = dict["nc-program_name"] ?: STRING_UNDEFINED,
            blank = blank,
            machiningTime = time,
            preview = preview,
            tools = tools,
            parts = parts
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

    private fun parseParts(elements: Elements): List<Part> {
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

    private fun parsePart(elements: List<Element>): Part {
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
        } ?: Blank(DOUBLE_UNDEFINED, DOUBLE_UNDEFINED)


        return Part(
            quantity = dict["part_number"]?.toInt() ?: INT_UNDEFINED,
            dimensions = dimensions,
            geoFilename = dict["geofile_name"]?.split("//")?.last()?.removeSuffix(".GEO") ?: STRING_UNDEFINED,
        )
    }

    companion object {
        const val STRING_UNDEFINED = "Undefined"
        const val INT_UNDEFINED = -1
        const val DOUBLE_UNDEFINED = -1.0
    }

}
