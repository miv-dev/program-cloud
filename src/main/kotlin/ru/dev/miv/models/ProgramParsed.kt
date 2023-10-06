package ru.dev.miv.models

import kotlinx.serialization.Serializable

@Serializable
data class ProgramParsed(
    val programId: String,
    val name: String,
    val programName: String,
    val blank: Blank,
    val machiningTime: Int,
    val preview: String,
    val tools: List<String> = emptyList(),
    val parts: List<Part> = emptyList(),
)

@Serializable
data class Blank(
    val width: Double,
    val length: Double,
    val height: Double = 0.0,
)

@Serializable
data class Part(
    val number: Int,
    val dimensions: Blank,
    val geoFilename: String,
)
