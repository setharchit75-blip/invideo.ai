package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.FuturisticAudioSynth
import com.example.data.model.VideoEntity
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberProgressBar
import com.example.ui.components.ProceduralVideoRenderer
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.DangerCoral
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import kotlinx.coroutines.delay

@Composable
fun PlayerScreen(
    video: VideoEntity,
    onBack: () -> Unit,
    onToggleFavorite: (id: String, current: Boolean) -> Unit,
    onIncrementLikes: (id: String) -> Unit,
    onRemix: (prompt: String, style: String, preset: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(true) }
    var progress by remember { mutableFloatStateOf(0f) }
    var isLooping by remember { mutableStateOf(true) }
    var isMuted by remember { mutableStateOf(FuturisticAudioSynth.isAudioMuted()) }
    var isFullscreen by remember { mutableStateOf(false) }
    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableFloatStateOf(0f) }
    var downloadComplete by remember { mutableStateOf(false) }
    var isLiked by remember { mutableStateOf(video.isFavorite) }
    var likesCount by remember { mutableIntStateOf(video.likes) }

    DisposableEffect(video.visualEffectPreset) {
        if (!isMuted) {
            FuturisticAudioSynth.startAmbient(video.visualEffectPreset)
        }
        onDispose {
            FuturisticAudioSynth.stopAmbient()
        }
    }

    LaunchedEffect(isPlaying, isLooping) {
        while (isPlaying) {
            delay(50)
            val newProgress = progress + (0.05f / video.duration)
            if (newProgress >= 1f) {
                if (isLooping) {
                    progress = 0f
                } else {
                    progress = 1f
                    isPlaying = false
                }
            } else {
                progress = newProgress
            }
        }
    }

    LaunchedEffect(isDownloading) {
        if (isDownloading) {
            downloadProgress = 0f
            while (downloadProgress < 1f) {
                delay(120)
                downloadProgress += 0.15f
            }
            downloadProgress = 1f
            isDownloading = false
            downloadComplete = true
            FuturisticAudioSynth.playCompletionChord()
        }
    }

    val currentSeconds = (progress * video.duration).toInt()
    val timeFormatted = String.format("%02d:%02d / %02d:%02d", currentSeconds / 60, currentSeconds % 60, video.duration / 60, video.duration % 60)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .testTag("player_screen"),
        contentPadding = if (isFullscreen) PaddingValues(0.dp) else PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (!isFullscreen) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("player_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = NeonCyan
                            )
                        }
                        Text(
                            text = "STUDIO RENDER PLAYER",
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    CyberBadge(
                        text = "${video.quality} @ ${video.fps}FPS",
                        color = ElectricViolet
                    )
                }
            }
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (isFullscreen) Modifier.fillMaxSize()
                        else Modifier
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.5.dp, CyberCardBorder, RoundedCornerShape(16.dp))
                    )
            ) {
                val inputRes = video.inputImageUrl?.toIntOrNull()
                ProceduralVideoRenderer(
                    preset = video.visualEffectPreset,
                    isPlaying = isPlaying,
                    progress = progress,
                    customImageRes = inputRes,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                            )
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Column {
                        Slider(
                            value = progress,
                            onValueChange = {
                                progress = it
                                isPlaying = false
                            },
                            onValueChangeFinished = {
                                isPlaying = true
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = NeonCyan,
                                activeTrackColor = NeonCyan,
                                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .testTag("video_scrubber")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        isPlaying = !isPlaying
                                        FuturisticAudioSynth.playBlip(700f, 30)
                                    },
                                    modifier = Modifier.testTag("play_pause_button")
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = if (isPlaying) "Pause" else "Play",
                                        tint = NeonCyan,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        isMuted = FuturisticAudioSynth.toggleMute()
                                        if (!isMuted) {
                                            FuturisticAudioSynth.startAmbient(video.visualEffectPreset)
                                        }
                                    },
                                    modifier = Modifier.testTag("audio_toggle_button")
                                ) {
                                    Icon(
                                        imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                                        contentDescription = "Audio Toggle",
                                        tint = if (isMuted) TextMuted else NeonGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Text(
                                    text = timeFormatted,
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        isLooping = !isLooping
                                        FuturisticAudioSynth.playBlip(850f, 30)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Loop,
                                        contentDescription = "Loop",
                                        tint = if (isLooping) NeonCyan else TextMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        isFullscreen = !isFullscreen
                                        FuturisticAudioSynth.playBlip(950f, 30)
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                        contentDescription = "Fullscreen",
                                        tint = TextPrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!isFullscreen) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CyberSurfaceVariant)
                            .border(1.dp, if (isLiked) DangerCoral else CyberCardBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                isLiked = !isLiked
                                if (isLiked) {
                                    likesCount++
                                    onIncrementLikes(video.id)
                                } else {
                                    likesCount = maxOf(0, likesCount - 1)
                                }
                                onToggleFavorite(video.id, !isLiked)
                                FuturisticAudioSynth.playBlip(1100f, 40)
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (isLiked) DangerCoral else TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$likesCount",
                                color = if (isLiked) DangerCoral else TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1.2f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CyberSurfaceVariant)
                            .border(1.dp, CyberCardBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                isDownloading = true
                                downloadComplete = false
                                FuturisticAudioSynth.playBlip(900f, 30)
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Download",
                                tint = if (downloadComplete) NeonGreen else NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (downloadComplete) "SAVED" else "DOWNLOAD",
                                color = if (downloadComplete) NeonGreen else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(CyberSurfaceVariant)
                            .border(1.dp, CyberCardBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, video.title)
                                    putExtra(Intent.EXTRA_TEXT, "Check out this AI Video generated with Nexus AI!\nPrompt: ${video.prompt}\nQuality: ${video.quality} @ ${video.fps}FPS")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share AI Video"))
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = ElectricViolet,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "SHARE",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            if (isDownloading) {
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "ENCODING MP4 FILE TO DEVICE...",
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "${(downloadProgress * 100).toInt()}%",
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        CyberProgressBar(progress = downloadProgress)
                    }
                }
            }

            item {
                CyberCard(tag = "video_details_card") {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = video.title,
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = video.prompt,
                            color = TextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "CAMERA MOTION", color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                Text(text = video.cameraMotion, color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text(text = "STYLE PRESET", color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                Text(text = video.style, color = ElectricViolet, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text(text = "ASPECT RATIO", color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                Text(text = video.aspectRatio, color = WarningAmber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        CyberButton(
                            text = "REMIX IN STUDIO",
                            icon = Icons.Default.Refresh,
                            onClick = {
                                onRemix(video.prompt, video.style, video.visualEffectPreset)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            tag = "remix_in_studio_button"
                        )
                    }
                }
            }
        }
    }
}
