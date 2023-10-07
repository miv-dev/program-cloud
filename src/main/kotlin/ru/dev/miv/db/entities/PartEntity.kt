package ru.dev.miv.db.entities

import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ru.dev.miv.db.tables.PartTable
import ru.dev.miv.models.PartModel

class PartEntity(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, PartEntity>(PartTable)

    var dimensions by PartTable.dimensions
    var quantity by PartTable.quantity
    var geoFilename by PartTable.geoFilename

    fun toModel() = PartModel(
        dimensions = Json.decodeFromString(dimensions),
        quantity = quantity,
        geoFilename = geoFilename
    )

}
