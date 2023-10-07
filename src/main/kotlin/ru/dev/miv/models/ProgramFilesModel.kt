package ru.dev.miv.models

import kotlinx.serialization.Serializable

@Serializable
data class ProgramFilesModel(
    val lst: FileModel?,
    val tmt: FileModel?,
    val preview: FileModel?,
)

