package ru.dev.miv.models

import kotlinx.serialization.Serializable
import ru.dev.miv.utils.serializers.UUIDSerializer
import java.util.UUID

@Serializable
data class UserModel (
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID? = null,
    val role: Role = Role.employee,
    val password: String,
    val email: String,
)