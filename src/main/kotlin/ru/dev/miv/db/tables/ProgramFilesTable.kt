package ru.dev.miv.db.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption

object ProgramFilesTable: UUIDTable("program_files") {
    val lstFile = reference("lst_file", FileTable, onDelete = ReferenceOption.CASCADE).nullable()
    val tmtFile = reference("tmt_file", FileTable, onDelete = ReferenceOption.CASCADE).nullable()
    val previewFile = reference("preview_file", FileTable, onDelete = ReferenceOption.CASCADE).nullable()
}




