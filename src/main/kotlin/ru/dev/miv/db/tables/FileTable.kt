package ru.dev.miv.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object FileTable: IntIdTable("file_table"){
    val path = varchar("path", length = 256)
    val lastUpdate = datetime("last_update" ).default(LocalDateTime.now())
}
