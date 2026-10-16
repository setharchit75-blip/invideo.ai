package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.AppRepository
import com.example.data.model.CreditTransaction
import com.example.data.model.Template
import com.example.data.model.VideoEntity
import com.example.ui.theme.CyberThemeMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppNavTab(val label: String) {
    HOME("Home"),
    STUDIO("Studio"),
    TEMPLATES("Templates"),
    VAULT("Vault"),
    CREDITS("Credits")
}

data class GenerationState(
    val isGenerating: Boolean = false,
    val progress: Int = 0,
    val stage: String = "",
    val prompt: String = "",
    val quality: String = "720p",
    val style: String = "Cyberpunk"
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = AppRepository(
        context = application,
        videoDao = database.videoDao(),
        creditDao = database.creditDao()
    )

    val credits: StateFlow<Int> = repository.credits
    val usedToday: StateFlow<Int> = repository.usedToday
    val streakDays: StateFlow<Int> = repository.streakDays
    val claimedBonusToday: StateFlow<Boolean> = repository.claimedBonusToday

    val allVideos: StateFlow<List<VideoEntity>> = repository.allVideos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val creditHistory: StateFlow<List<CreditTransaction>> = repository.creditHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val templates: List<Template> = repository.getTemplates()

    private val _currentTab = MutableStateFlow(AppNavTab.HOME)
    val currentTab = _currentTab.asStateFlow()

    private val _activePlayerVideo = MutableStateFlow<VideoEntity?>(null)
    val activePlayerVideo = _activePlayerVideo.asStateFlow()

    private val _generationState = MutableStateFlow(GenerationState())
    val generationState = _generationState.asStateFlow()

    private var generationJob: Job? = null

    private val _studioPrefill = MutableStateFlow(
        StudioPrefill("text_to_video", "", "Cyberpunk", "CYBER_CITY")
    )
    val studioPrefill = _studioPrefill.asStateFlow()

    private val _themeMode = MutableStateFlow(CyberThemeMode.CYBER_NEON)
    val themeMode = _themeMode.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode = _isDarkMode.asStateFlow()

    fun switchTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    fun openPlayer(video: VideoEntity) {
        _activePlayerVideo.value = video
    }

    fun closePlayer() {
        _activePlayerVideo.value = null
    }

    fun prefillStudio(mode: String, prompt: String, style: String, preset: String) {
        _studioPrefill.value = StudioPrefill(mode, prompt, style, preset)
        _activePlayerVideo.value = null
        _currentTab.value = AppNavTab.STUDIO
    }

    fun claimDailyBonus() {
        repository.claimDailyBonus()
    }

    fun redeemCoupon(code: String): Result<Int> {
        return repository.redeemCoupon(code)
    }

    fun purchasePackage(name: String, amount: Int) {
        repository.purchasePackage(name, amount)
    }

    fun toggleFavorite(id: String, current: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(id, current)
            if (_activePlayerVideo.value?.id == id) {
                _activePlayerVideo.value = _activePlayerVideo.value?.copy(isFavorite = !current)
            }
        }
    }

    fun incrementLikes(id: String) {
        viewModelScope.launch {
            repository.incrementLikes(id)
            if (_activePlayerVideo.value?.id == id) {
                val cur = _activePlayerVideo.value?.likes ?: 0
                _activePlayerVideo.value = _activePlayerVideo.value?.copy(likes = cur + 1)
            }
        }
    }

    fun deleteVideo(id: String) {
        viewModelScope.launch {
            repository.deleteVideo(id)
            if (_activePlayerVideo.value?.id == id) {
                _activePlayerVideo.value = null
            }
        }
    }

    fun startGeneration(
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
    ) {
        _generationState.value = GenerationState(
            isGenerating = true,
            progress = 5,
            stage = "Initializing Tensor Pipeline...",
            prompt = prompt,
            quality = quality,
            style = style
        )

        generationJob = viewModelScope.launch {
            try {
                val created = repository.generateCreation(
                    title = title,
                    prompt = prompt,
                    videoType = videoType,
                    style = style,
                    quality = quality,
                    fps = fps,
                    duration = duration,
                    cameraMotion = cameraMotion,
                    aspectRatio = aspectRatio,
                    inputImageRes = inputImageRes,
                    visualPreset = visualPreset
                ) { p, stageText ->
                    _generationState.value = _generationState.value.copy(
                        progress = p,
                        stage = stageText
                    )
                }

                _generationState.value = GenerationState(isGenerating = false)
                _activePlayerVideo.value = created
            } catch (_: Exception) {
                _generationState.value = GenerationState(isGenerating = false)
            }
        }
    }

    fun cancelGeneration() {
        generationJob?.cancel()
        generationJob = null
        _generationState.value = GenerationState(isGenerating = false)
    }

    fun cycleThemeMode() {
        val modes = CyberThemeMode.entries.toTypedArray()
        val nextIdx = (_themeMode.value.ordinal + 1) % modes.size
        _themeMode.value = modes[nextIdx]
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }
}

data class StudioPrefill(
    val mode: String,
    val prompt: String,
    val style: String,
    val preset: String
)
