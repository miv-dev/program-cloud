package ru.dev.miv.db.entities

import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ru.dev.miv.db.tables.ProgramPartTable
import ru.dev.miv.db.tables.ProgramTable
import ru.dev.miv.models.ProgramModel
import java.util.*

class ProgramEntity(uuid: EntityID<UUID>) : Entity<UUID>(uuid) {
    companion object : EntityClass<UUID, ProgramEntity>(ProgramTable)

    var programId by ProgramTable.programId
    var name by ProgramTable.name
    var blank by ProgramTable.blank
    var machiningTime by ProgramTable.machiningTime
    var files by ProgramFilesEntity referencedOn ProgramTable.files
    var tools by ProgramTable.tools
    var parts by PartEntity via ProgramPartTable
    var comment by ProgramTable.comment

    fun toModel() = ProgramModel(
        uuid = id.value,
        programId = programId,
        name = name,
        blank = Json.decodeFromString(blank),
        machiningTime = machiningTime,
        files = files.toModel(),
        tools = Json.decodeFromString(tools),
        parts = parts.map { it.toModel() },
        comment = comment
    )

    fun toModel(staticUrl: String) = ProgramModel(
        uuid = id.value,
        programId = programId,
        name = name,
        blank = Json.decodeFromString(blank),
        machiningTime = machiningTime,
        files = files.toModel(staticUrl),
        tools = Json.decodeFromString(tools),
        parts = parts.map { it.toModel() },
        comment = comment
    )

}



