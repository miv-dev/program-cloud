package ru.dev.miv.models

import kotlinx.serialization.Serializable
import ru.dev.miv.serializers.UUIDSerializer
import java.util.*

@Serializable
data class Program(
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID,
    val programId: String,
    val name: String,
//    val blank: Blank,
    val machiningTime: Int,
//    val tools: List<String> = emptyList(),
//    val parts: List<Part> = emptyList(),

    val tmt: String,
    val lst: String,
    val preview: String,
)
