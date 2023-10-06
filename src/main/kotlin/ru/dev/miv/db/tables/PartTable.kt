package ru.dev.miv.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object PartTable: IntIdTable("parts") {
    val dimensions = varchar("dimensions", length = 256)
    val quantity = integer("quantity")
    val geoFilename = varchar("geo_filename", length = 256)
}

