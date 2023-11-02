package ru.dev.miv.models

import kotlinx.serialization.Serializable
import ru.dev.miv.serializers.UUIDSerializer
import java.util.*

@Serializable
data class ProgramModel(
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID,
    val programId: String,
    val name: String,
    val blank: BlankModel,
    val machiningTime: Int,
    val files: ProgramFilesModel?,
    val tools: List<String>,
    val parts: List<PartModel>
)


