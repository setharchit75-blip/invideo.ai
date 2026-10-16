package com.example.data.model

data class Template(
    val id: String,
    val title: String,
    val description: String,
    val prompt: String,
    val category: String,
    val style: String,
    val quality: String = "1080p",
    val fps: Int = 30,
    val duration: Int = 15,
    val cameraMotion: String = "Orbit 360",
    val aspectRatio: String = "16:9",
    val drawableRes: Int,
    val usesCount: Int,
    val likesCount: Int,
    val visualPreset: String = "CYBER_CITY"
)
