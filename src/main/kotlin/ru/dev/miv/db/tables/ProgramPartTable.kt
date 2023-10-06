package ru.dev.miv.db.tables

import org.jetbrains.exposed.sql.Table

object ProgramPartTable: Table("program__part"){
    val program = reference("program", ProgramTable)
    val part = reference("part", PartTable)
}
