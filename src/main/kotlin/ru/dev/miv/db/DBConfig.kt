package ru.dev.miv.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database

object DBConfig {
    data class Credentials(
        var url: String,
        var username: String,
        var password: String,
    )


    fun setup(credentials: Credentials) {
        val config = HikariConfig().also { config ->
            config.driverClassName = DRIVER_CLASS_NAME
            config.transactionIsolation = TRANSACTION_ISOLATION
            config.jdbcUrl = credentials.url
            config.username = credentials.username
            config.password = credentials.password
        }
        Database.connect(HikariDataSource(config))
    }

    private const val DRIVER_CLASS_NAME = "org.postgresql.Driver"
    private const val TRANSACTION_ISOLATION = "TRANSACTION_REPEATABLE_READ"
}
