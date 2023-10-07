package ru.dev.miv.db.entities

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ru.dev.miv.db.tables.FileTable
import ru.dev.miv.models.FileModel

class FileEntity(id: EntityID<Int>) : Entity<Int>(id) {
    fun toModel() = FileModel(
        path,
        lastUpdate.toString()
    )

    companion object : EntityClass<Int, FileEntity>(FileTable)

    var path by FileTable.path
    val lastUpdate by FileTable.lastUpdate


}
