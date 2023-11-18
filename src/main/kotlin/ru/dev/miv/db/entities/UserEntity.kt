package ru.dev.miv.db.entities

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import ru.dev.miv.db.tables.UserTable
import ru.dev.miv.models.UserModel
import java.util.*

class UserEntity(id: EntityID<UUID>) : Entity<UUID>(id) {
    companion object : EntityClass<UUID, UserEntity>(UserTable)

    var password by UserTable.password
    var email by UserTable.email
    var role by UserTable.role

    fun toModel() = UserModel(
        uuid = id.value,
        email = email,
        role = role,
        password = password
    )


}