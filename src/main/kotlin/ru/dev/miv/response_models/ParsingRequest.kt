package ru.dev.miv.response_models

import kotlinx.serialization.Serializable
import ru.dev.miv.models.ProgramModel
@Serializable
data class ParsingRequest(
    val path: String,
    val program: String
)
