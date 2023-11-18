package ru.dev.miv.services

import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import ru.dev.miv.db.entities.UserEntity
import ru.dev.miv.db.tables.UserTable
import ru.dev.miv.models.UserModel
import java.util.*

class UserService {

    suspend fun users(): List<UserModel> = newSuspendedTransaction {
        UserEntity.all().map { it.toModel() }
    }

    suspend fun userByEmail(email: String): UserModel? = newSuspendedTransaction {
        UserEntity.find { UserTable.email eq email }.firstOrNull()?.toModel()
    }

    suspend fun userById(userId: String): UserModel = newSuspendedTransaction {
        UserEntity[UUID.fromString(userId)].toModel()
    }


    suspend fun login(email: String, password: String): UserModel {
        userByEmail(email)?.let { user: UserModel ->

            if (user.password == password) {
                return user
            } else {
                throw RuntimeException("Credentials aren't valid")
            }

        } ?: throw RuntimeException("User is not exist")
    }

}