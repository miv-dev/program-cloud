package ru.dev.miv.response_models

import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import ru.dev.miv.serializers.UUIDSerializer
import java.util.UUID

@Serializable
data class UploadResponse (
    @Serializable(with = UUIDSerializer::class)
    val programId: UUID,
    val uploadedFiles: List<String>,
    val failedFiles: List<String>
)
