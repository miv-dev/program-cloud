package ru.dev.miv.db.tables

import org.jetbrains.exposed.dao.id.UUIDTable

object ProgramTable : UUIDTable(name = "programs") {
    val programId = varchar("program_id", length = 16)
    val name = varchar("name", length = 256)
    val blank = varchar("blank", length = 256)
    val machiningTime = integer("machining_time")
    val files = reference("files", ProgramFilesTable)
}
