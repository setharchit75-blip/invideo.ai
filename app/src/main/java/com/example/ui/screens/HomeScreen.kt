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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.FuturisticAudioSynth
import com.example.data.model.Template
import com.example.data.model.VideoEntity
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.PulsingStatusDot
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber

@Composable
fun HomeScreen(
    credits: Int,
    usedToday: Int,
    streakDays: Int,
    claimedBonusToday: Boolean,
    recentVideos: List<VideoEntity>,
    templates: List<Template>,
    onClaimDailyBonus: () -> Unit,
    onNavigateStudio: (initialMode: String, initialPrompt: String, initialStyle: String, initialPreset: String) -> Unit,
    onOpenPlayer: (VideoEntity) -> Unit,
    onNavigateCredits: () -> Unit,
    onNavigateTemplates: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .testTag("home_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, NeonCyan, CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.cyber_android_portrait_1792072607019),
                            contentDescription = "User Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "NEXUS // CORE",
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            PulsingStatusDot(color = NeonGreen)
                        }
                        Text(
                            text = "AI Studio Online",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(CyberSurfaceVariant)
                        .border(1.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .clickable { onNavigateCredits() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("credits_pill"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = WarningAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$credits CR",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        item {
            CyberCard(tag = "daily_banner") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(CyberSurfaceVariant, Color(0xFF1E1B4B))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CyberBadge(text = "DAILY REFILL", color = NeonCyan)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "2,400 Free Credits/Day",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = "Streak: $streakDays d",
                                color = WarningAmber,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (claimedBonusToday) {
                                "Daily bonus claimed! Used today: $usedToday credits. Refills at midnight."
                            } else {
                                "Claim your Day $streakDays login bonus (+${50 + streakDays * 10} CR)!"
                            },
                            color = TextSecondary,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (!claimedBonusToday) {
                                CyberButton(
                                    text = "CLAIM +${50 + streakDays * 10} CR",
                                    icon = Icons.Default.Celebration,
                                    onClick = onClaimDailyBonus,
                                    modifier = Modifier.weight(1f),
                                    tag = "claim_bonus_button"
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                            }
                            CyberButton(
                                text = "CREDIT VAULT",
                                isPrimary = claimedBonusToday,
                                onClick = onNavigateCredits,
                                modifier = if (claimedBonusToday) Modifier.fillMaxWidth() else Modifier.weight(1f),
                                tag = "open_vault_button"
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "NEURAL CREATION MODES",
                color = NeonCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CreationModeCard(
                    title = "Text to Video",
                    sub = "Prompt -> 4K Video",
                    icon = Icons.Default.Videocam,
                    accent = NeonCyan,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onNavigateStudio("text_to_video", "", "Cyberpunk", "CYBER_CITY")
                    }
                )
                CreationModeCard(
                    title = "Image to Video",
                    sub = "Animate Static Art",
                    icon = Icons.Default.Image,
                    accent = ElectricViolet,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onNavigateStudio("image_to_video", "Animate motion with camera push in", "Cinematic", "CYBER_CITY")
                    }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CreationModeCard(
                    title = "Text to Image",
                    sub = "8K Concept Art",
                    icon = Icons.Default.AutoAwesome,
                    accent = ElectricViolet,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onNavigateStudio("text_to_image", "Ultra-detailed cybernetic android", "Hyper-Realistic", "CYBORG_SCAN")
                    }
                )
                CreationModeCard(
                    title = "Storyboard",
                    sub = "Multi-scene Script",
                    icon = Icons.Default.Subscriptions,
                    accent = NeonGreen,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onNavigateStudio("storyboard", "Sci-Fi epic space mission", "Sci-Fi VFX", "NEBULA_WARP")
                    }
                )
            }
        }

        item {
            CyberCard(tag = "prompt_of_day_card") {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CyberBadge(text = "DAILY PROMPT INSPIRATION", color = WarningAmber)
                        Text(
                            text = "COST: 9 CR",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"Bioluminescent ethereal dragon soaring over Neo-Tokyo rainy skybridges with neon lightning strike.\"",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        CyberButton(
                            text = "GENERATE THIS VIDEO",
                            icon = Icons.Default.PlayArrow,
                            onClick = {
                                onNavigateStudio(
                                    "text_to_video",
                                    "Bioluminescent ethereal dragon soaring over Neo-Tokyo rainy skybridges with neon lightning strike",
                                    "Cyberpunk",
                                    "DRAGON_VFX"
                                )
                            },
                            tag = "use_daily_prompt_button"
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TRENDING TEMPLATES",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "View All ->",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { onNavigateTemplates() }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(end = 16.dp)
            ) {
                items(templates) { tpl ->
                    TemplateHomeItem(
                        template = tpl,
                        onClick = {
                            onNavigateStudio(
                                "text_to_video",
                                tpl.prompt,
                                tpl.style,
                                tpl.visualPreset
                            )
                        }
                    )
                }
            }
        }

        item {
            Text(
                text = "RECENT RENDERS",
                color = NeonCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (recentVideos.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberSurfaceVariant)
                        .border(1.dp, CyberCardBorder, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No renders yet. Launch Studio above to create!",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(recentVideos.take(4)) { video ->
                RecentVideoItem(
                    video = video,
                    onClick = { onOpenPlayer(video) }
                )
            }
        }
    }
}

@Composable
fun CreationModeCard(
    title: String,
    sub: String,
    icon: ImageVector,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            .clickable {
                FuturisticAudioSynth.playBlip(800f, 30)
                onClick()
            },
        colors = CardDefaults.cardColors(containerColor = CyberSurface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = sub,
                color = TextSecondary,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun TemplateHomeItem(
    template: Template,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(220.dp)
            .height(170.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, CyberCardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = template.drawableRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CyberBadge(text = template.quality, color = NeonCyan)
                CyberBadge(text = template.style, color = ElectricViolet)
            }
            Column {
                Text(
                    text = template.title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${template.usesCount / 1000}k uses",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "REMIX ->",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun RecentVideoItem(
    video: VideoEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, CyberCardBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("recent_video_${video.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CyberSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                val inputRes = video.inputImageUrl?.toIntOrNull()
                val defaultImg = when (video.visualEffectPreset) {
                    "CYBER_CITY" -> R.drawable.banner_cyber_city_1792072581849
                    "DRAGON_VFX" -> R.drawable.alien_biolum_vfx_1792072606950
                    else -> R.drawable.cyber_android_portrait_1792072607019
                }
                Image(
                    painter = painterResource(id = inputRes ?: defaultImg),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = NeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = video.title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = video.prompt,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    CyberBadge(text = video.quality, color = NeonCyan)
                    CyberBadge(text = "${video.fps} FPS", color = ElectricViolet)
                    CyberBadge(text = "${video.duration}s", color = WarningAmber)
                }
            }
        }
    }
}
