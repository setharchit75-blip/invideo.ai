package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberPink
import com.example.ui.theme.HologramBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class Particle(
    val xRel: Float,
    val yRel: Float,
    val speed: Float,
    val length: Float,
    val size: Float,
    val color: Color
)

@Composable
fun ProceduralVideoRenderer(
    preset: String,
    isPlaying: Boolean,
    progress: Float,
    customImageRes: Int? = null,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "video_vfx")
    val animPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "animPhase"
    )

    val effectivePhase = (progress + (if (isPlaying) animPhase * 0.2f else 0f)) % 1f

    val defaultRes = when (preset) {
        "CYBER_CITY" -> R.drawable.banner_cyber_city_1792072581849
        "DRAGON_VFX" -> R.drawable.alien_biolum_vfx_1792072606950
        "CYBORG_SCAN" -> R.drawable.cyber_android_portrait_1792072607019
        else -> R.drawable.banner_cyber_city_1792072581849
    }
    val imageRes = customImageRes ?: defaultRes

    val particles = remember(preset) {
        val rand = Random(42)
        List(40) {
            Particle(
                xRel = rand.nextFloat(),
                yRel = rand.nextFloat(),
                speed = 0.5f + rand.nextFloat() * 1.5f,
                length = 15f + rand.nextFloat() * 45f,
                size = 2f + rand.nextFloat() * 4f,
                color = when (preset) {
                    "CYBER_CITY" -> if (rand.nextBoolean()) NeonCyan else CyberPink
                    "DRAGON_VFX" -> if (rand.nextBoolean()) NeonCyan else NeonGreen
                    "CYBORG_SCAN" -> NeonCyan
                    else -> HologramBlue
                }
            )
        }
    }

    Box(modifier = modifier.background(CyberBackground)) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Black.copy(alpha = 0.35f),
                        Color.Transparent,
                        Color.Black.copy(alpha = 0.65f)
                    )
                )
            )

            when (preset) {
                "CYBER_CITY" -> {
                    particles.forEach { p ->
                        val currentY = ((p.yRel + effectivePhase * p.speed * 2f) % 1f) * h
                        val currentX = (p.xRel * w)
                        drawLine(
                            color = p.color.copy(alpha = 0.7f),
                            start = Offset(currentX, currentY),
                            end = Offset(currentX - 5f, currentY + p.length),
                            strokeWidth = p.size
                        )
                    }

                    val speederX = ((effectivePhase * 2.2f) % 1.4f - 0.2f) * w
                    val speederY = h * 0.42f
                    drawLine(
                        brush = Brush.horizontalGradient(
                            listOf(Color.Transparent, NeonCyan, Color.White, Color.Transparent),
                            startX = speederX - 120f,
                            endX = speederX + 120f
                        ),
                        start = Offset(speederX - 120f, speederY),
                        end = Offset(speederX + 120f, speederY),
                        strokeWidth = 4f
                    )
                }

                "DRAGON_VFX" -> {
                    particles.forEachIndexed { i, p ->
                        val currentY = ((1f - (p.yRel + effectivePhase * p.speed) % 1f)) * h
                        val waveOffset = sin((effectivePhase * 6.28f + i)) * 30f
                        val currentX = (p.xRel * w) + waveOffset

                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(p.color, p.color.copy(alpha = 0f)),
                                center = Offset(currentX, currentY),
                                radius = p.size * 5f
                            ),
                            radius = p.size * 5f,
                            center = Offset(currentX, currentY)
                        )
                        drawCircle(
                            color = Color.White,
                            radius = p.size,
                            center = Offset(currentX, currentY)
                        )
                    }
                }

                "CYBORG_SCAN" -> {
                    val scanY = (sin(effectivePhase * 3.14159f * 2f) * 0.4f + 0.5f) * h
                    drawLine(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, NeonCyan.copy(alpha = 0.8f), Color.Transparent),
                            startY = scanY - 20f,
                            endY = scanY + 20f
                        ),
                        start = Offset(0f, scanY),
                        end = Offset(w, scanY),
                        strokeWidth = 2.5f
                    )

                    val centerX = w * 0.5f
                    val centerY = h * 0.45f
                    val radius = w * 0.22f

                    rotate(degrees = effectivePhase * 360f, pivot = Offset(centerX, centerY)) {
                        drawCircle(
                            color = NeonCyan.copy(alpha = 0.4f),
                            radius = radius,
                            center = Offset(centerX, centerY),
                            style = Stroke(width = 2f)
                        )
                        for (angle in 0 until 360 step 45) {
                            val rad = Math.toRadians(angle.toDouble())
                            val x1 = centerX + (radius - 12f) * cos(rad).toFloat()
                            val y1 = centerY + (radius - 12f) * sin(rad).toFloat()
                            val x2 = centerX + (radius + 8f) * cos(rad).toFloat()
                            val y2 = centerY + (radius + 8f) * sin(rad).toFloat()
                            drawLine(
                                color = NeonCyan.copy(alpha = 0.6f),
                                start = Offset(x1, y1),
                                end = Offset(x2, y2),
                                strokeWidth = 2f
                            )
                        }
                    }
                }

                else -> {
                    val centerX = w * 0.5f
                    val centerY = h * 0.5f
                    particles.forEachIndexed { i, p ->
                        val angle = (i * 360f / particles.size) * (Math.PI / 180f)
                        val dist = ((p.yRel + effectivePhase * 2.5f) % 1f) * (w * 0.8f)
                        val x = centerX + cos(angle).toFloat() * dist
                        val y = centerY + sin(angle).toFloat() * dist

                        val length = dist * 0.2f
                        val xEnd = x + cos(angle).toFloat() * length
                        val yEnd = y + sin(angle).toFloat() * length

                        drawLine(
                            color = p.color.copy(alpha = (dist / (w * 0.8f)).coerceIn(0.2f, 1f)),
                            start = Offset(x, y),
                            end = Offset(xEnd, yEnd),
                            strokeWidth = p.size
                        )
                    }
                }
            }
        }
    }
}
