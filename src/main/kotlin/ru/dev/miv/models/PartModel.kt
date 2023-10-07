package ru.dev.miv.models

import kotlinx.serialization.Serializable

@Serializable
data class PartModel(
    val dimensions: Blank,
    val quantity: Int,
    val geoFilename: String,
)
