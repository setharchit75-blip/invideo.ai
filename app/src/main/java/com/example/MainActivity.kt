package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.FolderSpecial
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.FuturisticAudioSynth
import com.example.ui.AppNavTab
import com.example.ui.MainViewModel
import com.example.ui.components.CyberBadge
import com.example.ui.components.PulsingStatusDot
import com.example.ui.screens.CreditsScreen
import com.example.ui.screens.GeneratingScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PlayerScreen
import com.example.ui.screens.StudioScreen
import com.example.ui.screens.TemplatesScreen
import com.example.ui.screens.VaultScreen
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.WarningAmber

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            val themeMode by viewModel.themeMode.collectAsState()

            MyApplicationTheme(
                darkTheme = isDarkMode,
                themeMode = themeMode
            ) {
                MainContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainContent(viewModel: MainViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val activePlayerVideo by viewModel.activePlayerVideo.collectAsState()
    val genState by viewModel.generationState.collectAsState()

    val credits by viewModel.credits.collectAsState()
    val usedToday by viewModel.usedToday.collectAsState()
    val streakDays by viewModel.streakDays.collectAsState()
    val claimedBonusToday by viewModel.claimedBonusToday.collectAsState()
    val allVideos by viewModel.allVideos.collectAsState()
    val creditHistory by viewModel.creditHistory.collectAsState()
    val studioPrefill by viewModel.studioPrefill.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()

    var isMuted by remember { mutableStateOf(FuturisticAudioSynth.isAudioMuted()) }

    BackHandler(enabled = activePlayerVideo != null || currentTab != AppNavTab.HOME) {
        if (activePlayerVideo != null) {
            viewModel.closePlayer()
        } else if (currentTab != AppNavTab.HOME) {
            viewModel.switchTab(AppNavTab.HOME)
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(CyberBackground)) {
        if (genState.isGenerating) {
            GeneratingScreen(
                progressPercent = genState.progress,
                statusStage = genState.stage,
                prompt = genState.prompt,
                quality = genState.quality,
                style = genState.style,
                onCancel = { viewModel.cancelGeneration() }
            )
        } else if (activePlayerVideo != null) {
            PlayerScreen(
                video = activePlayerVideo!!,
                onBack = { viewModel.closePlayer() },
                onToggleFavorite = { id, cur -> viewModel.toggleFavorite(id, cur) },
                onIncrementLikes = { id -> viewModel.incrementLikes(id) },
                onRemix = { prompt, style, preset ->
                    viewModel.prefillStudio("text_to_video", prompt, style, preset)
                },
                modifier = Modifier.statusBarsPadding().navigationBarsPadding()
            )
        } else {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = CyberBackground,
                topBar = {
                    CyberTopBar(
                        credits = credits,
                        themeName = themeMode.displayName,
                        isDarkMode = isDarkMode,
                        isMuted = isMuted,
                        onCycleTheme = { viewModel.cycleThemeMode() },
                        onToggleDark = { viewModel.toggleDarkMode() },
                        onToggleMute = {
                            isMuted = FuturisticAudioSynth.toggleMute()
                        },
                        onNavigateCredits = { viewModel.switchTab(AppNavTab.CREDITS) }
                    )
                },
                bottomBar = {
                    CyberBottomNavBar(
                        currentTab = currentTab,
                        onTabSelected = { tab ->
                            FuturisticAudioSynth.playBlip(750f, 30)
                            viewModel.switchTab(tab)
                        }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    AnimatedContent(
                        targetState = currentTab,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "tab_transition"
                    ) { tab ->
                        when (tab) {
                            AppNavTab.HOME -> {
                                HomeScreen(
                                    credits = credits,
                                    usedToday = usedToday,
                                    streakDays = streakDays,
                                    claimedBonusToday = claimedBonusToday,
                                    recentVideos = allVideos,
                                    templates = viewModel.templates,
                                    onClaimDailyBonus = { viewModel.claimDailyBonus() },
                                    onNavigateStudio = { mode, prompt, style, preset ->
                                        viewModel.prefillStudio(mode, prompt, style, preset)
                                    },
                                    onOpenPlayer = { vid -> viewModel.openPlayer(vid) },
                                    onNavigateCredits = { viewModel.switchTab(AppNavTab.CREDITS) },
                                    onNavigateTemplates = { viewModel.switchTab(AppNavTab.TEMPLATES) }
                                )
                            }

                            AppNavTab.STUDIO -> {
                                StudioScreen(
                                    currentCredits = credits,
                                    initialMode = studioPrefill.mode,
                                    initialPrompt = studioPrefill.prompt,
                                    initialStyle = studioPrefill.style,
                                    initialPreset = studioPrefill.preset,
                                    onGenerate = { title, prompt, vType, style, quality, fps, dur, cam, aspect, img, preset ->
                                        viewModel.startGeneration(
                                            title = title,
                                            prompt = prompt,
                                            videoType = vType,
                                            style = style,
                                            quality = quality,
                                            fps = fps,
                                            duration = dur,
                                            cameraMotion = cam,
                                            aspectRatio = aspect,
                                            inputImageRes = img,
                                            visualPreset = preset
                                        )
                                    }
                                )
                            }

                            AppNavTab.TEMPLATES -> {
                                TemplatesScreen(
                                    templates = viewModel.templates,
                                    onSelectTemplate = { tpl ->
                                        viewModel.prefillStudio(
                                            "text_to_video",
                                            tpl.prompt,
                                            tpl.style,
                                            tpl.visualPreset
                                        )
                                    }
                                )
                            }

                            AppNavTab.VAULT -> {
                                VaultScreen(
                                    videos = allVideos,
                                    onOpenPlayer = { vid -> viewModel.openPlayer(vid) },
                                    onToggleFavorite = { id, cur -> viewModel.toggleFavorite(id, cur) },
                                    onDeleteVideo = { id -> viewModel.deleteVideo(id) }
                                )
                            }

                            AppNavTab.CREDITS -> {
                                CreditsScreen(
                                    credits = credits,
                                    usedToday = usedToday,
                                    transactions = creditHistory,
                                    onRedeemCoupon = { code -> viewModel.redeemCoupon(code) },
                                    onPurchasePackage = { name, amt -> viewModel.purchasePackage(name, amt) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CyberTopBar(
    credits: Int,
    themeName: String,
    isDarkMode: Boolean,
    isMuted: Boolean,
    onCycleTheme: () -> Unit,
    onToggleDark: () -> Unit,
    onToggleMute: () -> Unit,
    onNavigateCredits: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CyberSurface)
            .statusBarsPadding()
            .border(0.5.dp, CyberCardBorder)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(NeonCyan)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "NEXUS // AI",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                PulsingStatusDot(color = NeonGreen)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                CyberBadge(
                    text = themeName,
                    color = NeonCyan,
                    modifier = Modifier.clickable {
                        FuturisticAudioSynth.playBlip(900f, 30)
                        onCycleTheme()
                    }
                )

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = onToggleMute,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                        contentDescription = "Mute",
                        tint = if (isMuted) TextMuted else NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = {
                        FuturisticAudioSynth.playBlip(1000f, 30)
                        onToggleDark()
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = "Dark mode",
                        tint = if (isDarkMode) NeonCyan else WarningAmber,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(CyberSurfaceVariant)
                        .border(1.dp, WarningAmber.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .clickable { onNavigateCredits() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = WarningAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "$credits",
                            color = WarningAmber,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CyberBottomNavBar(
    currentTab: AppNavTab,
    onTabSelected: (AppNavTab) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CyberSurface)
            .navigationBarsPadding()
            .border(1.dp, CyberCardBorder)
            .padding(vertical = 6.dp, horizontal = 12.dp)
            .testTag("cyber_bottom_nav")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tabs = listOf(
                Pair(AppNavTab.HOME, Pair(Icons.Default.Home, Icons.Outlined.Home)),
                Pair(AppNavTab.STUDIO, Pair(Icons.Default.AutoAwesome, Icons.Outlined.AutoAwesome)),
                Pair(AppNavTab.TEMPLATES, Pair(Icons.Default.Subscriptions, Icons.Outlined.Subscriptions)),
                Pair(AppNavTab.VAULT, Pair(Icons.Default.FolderSpecial, Icons.Outlined.FolderSpecial)),
                Pair(AppNavTab.CREDITS, Pair(Icons.Default.Bolt, Icons.Outlined.Bolt))
            )

            tabs.forEach { (tab, icons) ->
                val isSelected = (currentTab == tab)
                val interactionSource = remember { MutableInteractionSource() }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = interactionSource,
                            indication = ripple(color = NeonCyan)
                        ) {
                            onTabSelected(tab)
                        }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .testTag("nav_item_${tab.name.lowercase()}")
                ) {
                    Box(
                        modifier = Modifier
                            .height(28.dp)
                            .then(
                                if (isSelected) Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(NeonCyan.copy(alpha = 0.18f))
                                    .padding(horizontal = 12.dp)
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) icons.first else icons.second,
                            contentDescription = tab.label,
                            tint = if (isSelected) NeonCyan else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tab.label,
                        color = if (isSelected) NeonCyan else TextMuted,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
