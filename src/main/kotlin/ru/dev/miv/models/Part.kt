package ru.dev.miv.models

import kotlinx.serialization.Serializable

@Serializable
data class Part(
    val quantity: Int,
    val dimensions: Blank,
    val geoFilename: String,
)
