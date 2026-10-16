package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val videoType: String = "text_to_video",
    val prompt: String,
    val inputImageUrl: String? = null,
    val outputVideoUrl: String? = null,
    val outputImageUrl: String? = null,
    val thumbnailUrl: String? = null,
    val duration: Int = 15,
    val quality: String = "720p",
    val fps: Int = 30,
    val creditsUsed: Int = 9,
    val status: String = "completed",
    val views: Int = 1,
    val likes: Int = 0,
    val isFavorite: Boolean = false,
    val aiModelUsed: String = "Nexus Gen-3 Turbo",
    val style: String = "Cyberpunk",
    val cameraMotion: String = "Orbit 360",
    val aspectRatio: String = "16:9",
    val visualEffectPreset: String = "CYBER_CITY",
    val createdAt: Long = System.currentTimeMillis()
)
