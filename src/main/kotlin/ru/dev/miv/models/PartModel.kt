package ru.dev.miv.models

import kotlinx.serialization.Serializable

@Serializable
data class PartModel(
    val dimensions: BlankModel,
    val quantity: Int,
    val geoFilename: String,
)
