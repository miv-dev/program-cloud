package ru.dev.miv.db.tables

import org.jetbrains.exposed.dao.id.IntIdTable


object TokensTable: IntIdTable("refresh_tokens") {
    val userId = uuid("user_id")
    val refreshToken = varchar("refresh_token", 300)
    val expiresAt = long("expires_at")
}
