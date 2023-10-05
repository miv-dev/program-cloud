package ru.dev.miv.db.entities

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ru.dev.miv.db.tables.ProgramTable
import ru.dev.miv.models.Blank
import ru.dev.miv.serializers.UUIDSerializer
import java.util.*

class ProgramEntity(uuid: EntityID<UUID>) : Entity<UUID>(uuid) {
    companion object : EntityClass<UUID, ProgramEntity>(ProgramTable)

    var programId by ProgramTable.programId
    var name by ProgramTable.name
    var blank by ProgramTable.blank
    var machiningTime by ProgramTable.machiningTime
    var files by ProgramFilesEntity referencedOn ProgramTable.files

    fun toModel() = ProgramModel(
        id.value,
        programId,
        name,
        Json.decodeFromString(blank),
        machiningTime,
        files.toModel()
    )
}


@Serializable
data class ProgramModel(
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID,
    val programId: String,
    val name: String,
    val blank: Blank,
    val machiningTime: Int,
    val files: ProgramFilesModel
)

@Serializable
data class ProgramFilesModel(
    val lst: FileModel?,
    val tmt: FileModel?,
    val preview: FileModel?,
)

@Serializable
data class FileModel(
    val path: String,
    val lastUpdate: String
)
