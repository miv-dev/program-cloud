package ru.dev.miv.db.tables

import org.jetbrains.exposed.dao.id.UUIDTable
import ru.dev.miv.models.Role

object UserTable: UUIDTable("users") {
    val email = varchar("email", length = 64)
    val password = varchar("password", length = 32)
    val role = enumerationByName<Role>("role", length = 64)
}