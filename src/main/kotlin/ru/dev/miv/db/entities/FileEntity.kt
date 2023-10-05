package ru.dev.miv.db.entities

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ru.dev.miv.db.tables.FileTable

class FileEntity(id: EntityID<Int>) : Entity<Int>(id) {

    companion object : EntityClass<Int, FileEntity>(FileTable)

    var path by FileTable.path
    val lastUpdate by FileTable.path


}
