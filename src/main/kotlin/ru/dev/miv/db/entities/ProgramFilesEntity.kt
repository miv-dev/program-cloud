package ru.dev.miv.db.entities

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ru.dev.miv.db.tables.ProgramFilesTable
import java.util.*

class ProgramFilesEntity(uuid: EntityID<UUID>): Entity<UUID>(uuid) {
    fun toModel(): ProgramFilesModel = ProgramFilesModel(
        lstFile?.toModel(),
        tmtFile?.toModel(),
        previewFile?.toModel()
    )

    companion object: EntityClass<UUID, ProgramFilesEntity>(ProgramFilesTable)

    var lstFile by FileEntity optionalReferencedOn  ProgramFilesTable.lstFile
    var tmtFile by FileEntity optionalReferencedOn ProgramFilesTable.tmtFile
    var previewFile by FileEntity optionalReferencedOn ProgramFilesTable.previewFile
}
