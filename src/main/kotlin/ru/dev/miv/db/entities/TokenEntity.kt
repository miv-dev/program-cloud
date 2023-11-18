package ru.dev.miv.db.entities

import ru.dev.miv.db.tables.TokensTable
import dev.miv.models.RefreshTokenFromDB
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
class TokenEntity(id: EntityID<Int>) : Entity<Int>(id) {
    companion object : EntityClass<Int, TokenEntity>(TokensTable)

    var userId by TokensTable.userId
    var refreshToken by TokensTable.refreshToken
    var expiresAt by TokensTable.expiresAt

    fun toDomain() = RefreshTokenFromDB(userId, refreshToken, expiresAt)
}
