package ru.dev.miv.models

import kotlinx.serialization.Serializable

@Serializable
data class FileModel(
    val path: String,
    val lastUpdate: String
)
