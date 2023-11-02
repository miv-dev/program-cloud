package ru.dev.miv.models

import kotlinx.serialization.Serializable

@Serializable
data class BlankModel(
    val width: Double,
    val length: Double,
    val height: Double = 0.0,
)
