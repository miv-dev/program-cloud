package ru.dev.miv.models

import kotlinx.serialization.Serializable

@Serializable
data class FileModel(
    val path: String?= null ,
    val lastUpdate: String?= null,
    val url: String = "",
)
