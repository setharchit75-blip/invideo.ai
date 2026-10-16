package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.FuturisticAudioSynth
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberChip
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberPink
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.DangerCoral
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber

@Composable
fun StudioScreen(
    currentCredits: Int,
    initialMode: String = "text_to_video",
    initialPrompt: String = "",
    initialStyle: String = "Cyberpunk",
    initialPreset: String = "CYBER_CITY",
    onGenerate: (
        title: String,
        prompt: String,
        videoType: String,
        style: String,
        quality: String,
        fps: Int,
        duration: Int,
        cameraMotion: String,
        aspectRatio: String,
        inputImageRes: Int?,
        visualPreset: String
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var mode by remember { mutableStateOf(initialMode) }
    var prompt by remember { mutableStateOf(initialPrompt) }
    var title by remember { mutableStateOf("") }
    var selectedStyle by remember { mutableStateOf(initialStyle) }
    var selectedQuality by remember { mutableStateOf("720p") }
    var selectedFps by remember { mutableIntStateOf(30) }
    var selectedDuration by remember { mutableIntStateOf(15) }
    var selectedCamera by remember { mutableStateOf("Orbit 360") }
    var selectedAspect by remember { mutableStateOf("16:9") }
    var motionBucket by remember { mutableFloatStateOf(127f) }
    var selectedImageIndex by remember { mutableIntStateOf(0) }
    var visualPreset by remember { mutableStateOf(initialPreset) }

    val presetImages = listOf(
        Pair(R.drawable.banner_cyber_city_1792072581849, "CYBER_CITY"),
        Pair(R.drawable.alien_biolum_vfx_1792072606950, "DRAGON_VFX"),
        Pair(R.drawable.cyber_android_portrait_1792072607019, "CYBORG_SCAN")
    )

    val styles = listOf(
        "Cyberpunk", "Cinematic", "Unreal Engine 5", "Anime",
        "Hyper-Realistic", "Dark Fantasy", "Retro Sci-Fi", "3D Pixar"
    )

    val cameras = listOf(
        "Orbit 360", "Push In / Zoom", "Drone Flyby", "Pan Right", "Tilt Up", "Static"
    )

    val qualities = listOf(
        Pair("480p", 3),
        Pair("720p", 9),
        Pair("1080p", 15),
        Pair("4k", 25)
    )

    val surprisePrompts = listOf(
        "Neon rain reflections on chrome cybernetic motorcycle speeding through Tokyo subterranean tunnel",
        "Glowing bioluminescent alien phoenix spreading cyan plasma wings above alien mountain peaks",
        "Deep space orbital station with giant spinning gravity rings docking with star cruiser",
        "Futuristic android samurai practicing katana forms amidst floating neon cherry blossoms"
    )

    val creditsNeeded = qualities.find { it.first.equals(selectedQuality, ignoreCase = true) }?.second ?: 9
    val canAfford = currentCredits >= creditsNeeded

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .testTag("studio_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "NEURAL STUDIO CONSOLE",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "AI Video Synthesizer",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                CyberBadge(
                    text = "ENGINE: NEXUS GEN-3",
                    color = ElectricViolet
                )
            }
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val modes = listOf(
                    Pair("text_to_video", "Text to Video"),
                    Pair("image_to_video", "Image to Video"),
                    Pair("text_to_image", "Text to Image"),
                    Pair("storyboard", "Storyboard")
                )
                items(modes) { (id, label) ->
                    CyberChip(
                        label = label,
                        selected = (mode == id),
                        onClick = {
                            mode = id
                            if (id == "image_to_video" && prompt.isBlank()) {
                                prompt = "Animate motion with fluid camera movement and realistic lighting"
                            }
                        },
                        accentColor = if (id == "text_to_video") NeonCyan else if (id == "image_to_video") ElectricViolet else CyberPink,
                        tag = "mode_tab_$id"
                    )
                }
            }
        }

        if (mode == "image_to_video") {
            item {
                CyberCard(tag = "image_selector_card") {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "SOURCE IMAGE SELECTION",
                            color = ElectricViolet,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            presetImages.forEachIndexed { index, (drawable, presetKey) ->
                                val isSelected = (selectedImageIndex == index)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(80.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .border(
                                            2.dp,
                                            if (isSelected) ElectricViolet else CyberCardBorder,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            selectedImageIndex = index
                                            visualPreset = presetKey
                                            FuturisticAudioSynth.playBlip(900f, 30)
                                        }
                                ) {
                                    Image(
                                        painter = painterResource(id = drawable),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(ElectricViolet.copy(alpha = 0.25f))
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Motion Bucket Intensity: ${motionBucket.toInt()}",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Slider(
                            value = motionBucket,
                            onValueChange = { motionBucket = it },
                            valueRange = 1f..255f,
                            colors = SliderDefaults.colors(
                                thumbColor = ElectricViolet,
                                activeTrackColor = ElectricViolet,
                                inactiveTrackColor = CyberSurfaceVariant
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        item {
            CyberCard(tag = "prompt_card") {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "NEURAL PROMPT",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "${prompt.length} / 1000",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = prompt,
                        onValueChange = { if (it.length <= 1000) prompt = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .testTag("prompt_input"),
                        placeholder = {
                            Text(
                                text = "Describe your scene in detail (e.g. cyber city flight, cinematic lighting, 8k render...)",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CyberSurfaceVariant,
                            unfocusedContainerColor = CyberSurfaceVariant,
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = CyberCardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        CyberChip(
                            label = "Enhance Prompt",
                            selected = false,
                            icon = Icons.Default.AutoAwesome,
                            onClick = {
                                if (prompt.isNotBlank()) {
                                    prompt = "$prompt, 8k resolution, cinematic lighting, photorealistic textures, volumetric fog, octane render, dynamic $selectedCamera"
                                    FuturisticAudioSynth.playBlip(1200f, 50)
                                }
                            },
                            accentColor = NeonCyan,
                            tag = "enhance_prompt_button"
                        )

                        CyberChip(
                            label = "Surprise Me",
                            selected = false,
                            icon = Icons.Default.Casino,
                            onClick = {
                                prompt = surprisePrompts.random()
                                FuturisticAudioSynth.playBlip(950f, 30)
                            },
                            accentColor = WarningAmber,
                            tag = "surprise_prompt_button"
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "ARTISTIC & RENDER STYLE",
                color = NeonCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(styles) { st ->
                    CyberChip(
                        label = st,
                        selected = (selectedStyle == st),
                        onClick = {
                            selectedStyle = st
                            visualPreset = when (st) {
                                "Cyberpunk" -> "CYBER_CITY"
                                "Unreal Engine 5" -> "DRAGON_VFX"
                                "Hyper-Realistic" -> "CYBORG_SCAN"
                                else -> "NEBULA_WARP"
                            }
                        },
                        accentColor = ElectricViolet,
                        tag = "style_chip_$st"
                    )
                }
            }
        }

        item {
            Text(
                text = "CAMERA MOVEMENT",
                color = NeonCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(cameras) { cam ->
                    CyberChip(
                        label = cam,
                        selected = (selectedCamera == cam),
                        onClick = { selectedCamera = cam },
                        accentColor = NeonCyan,
                        tag = "camera_chip_$cam"
                    )
                }
            }
        }

        item {
            CyberCard(tag = "quality_card") {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "RESOLUTION & RENDER SPECS",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        qualities.forEach { (q, cr) ->
                            val isSel = (selectedQuality.equals(q, ignoreCase = true))
                            CyberChip(
                                label = "$q ($cr CR)",
                                selected = isSel,
                                onClick = { selectedQuality = q },
                                modifier = Modifier.weight(1f),
                                accentColor = if (q == "4k") CyberPink else NeonCyan,
                                tag = "quality_chip_$q"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Frame Rate (FPS)",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(24, 30, 60).forEach { f ->
                                CyberBadge(
                                    text = "$f FPS",
                                    color = if (selectedFps == f) NeonCyan else TextMuted,
                                    modifier = Modifier.clickable { selectedFps = f }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Duration",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(5, 10, 15, 30).forEach { d ->
                                CyberBadge(
                                    text = "${d}s",
                                    color = if (selectedDuration == d) WarningAmber else TextMuted,
                                    modifier = Modifier.clickable { selectedDuration = d }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Aspect Ratio",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("16:9", "9:16", "1:1").forEach { a ->
                                CyberBadge(
                                    text = a,
                                    color = if (selectedAspect == a) ElectricViolet else TextMuted,
                                    modifier = Modifier.clickable { selectedAspect = a }
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            CyberCard(tag = "credit_cost_card") {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Credits Required: $creditsNeeded CR",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Your Balance: $currentCredits CR",
                                color = if (canAfford) NeonGreen else DangerCoral,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        CyberBadge(
                            text = if (canAfford) "FUNDS READY" else "LOW BALANCE",
                            color = if (canAfford) NeonGreen else DangerCoral
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    CyberButton(
                        text = if (canAfford) "SYNTHESIZE $selectedQuality VIDEO" else "INSUFFICIENT CREDITS",
                        icon = Icons.Default.PlayArrow,
                        enabled = canAfford && prompt.isNotBlank(),
                        onClick = {
                            val presetImg = if (mode == "image_to_video") {
                                presetImages[selectedImageIndex].first
                            } else null
                            onGenerate(
                                title,
                                prompt,
                                mode,
                                selectedStyle,
                                selectedQuality,
                                selectedFps,
                                selectedDuration,
                                selectedCamera,
                                selectedAspect,
                                presetImg,
                                visualPreset
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        tag = "start_generate_button"
                    )
                }
            }
        }
    }
}
