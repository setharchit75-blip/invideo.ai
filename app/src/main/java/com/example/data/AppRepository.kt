package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.R
import com.example.data.dao.CreditDao
import com.example.data.dao.VideoDao
import com.example.data.model.CreditTransaction
import com.example.data.model.Template
import com.example.data.model.VideoEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

class AppRepository(
    private val context: Context,
    private val videoDao: VideoDao,
    private val creditDao: CreditDao
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("nexus_ai_prefs", Context.MODE_PRIVATE)

    private val _credits = MutableStateFlow(2400)
    val credits = _credits.asStateFlow()

    private val _usedToday = MutableStateFlow(0)
    val usedToday = _usedToday.asStateFlow()

    private val _streakDays = MutableStateFlow(1)
    val streakDays = _streakDays.asStateFlow()

    private val _claimedBonusToday = MutableStateFlow(false)
    val claimedBonusToday = _claimedBonusToday.asStateFlow()

    val allVideos: Flow<List<VideoEntity>> = videoDao.getAllVideos()
    val favoriteVideos: Flow<List<VideoEntity>> = videoDao.getFavoriteVideos()
    val creditHistory: Flow<List<CreditTransaction>> = creditDao.getAllTransactions()

    init {
        loadCreditState()
        checkAndResetDailyCredits()
        initializeInitialCreationsIfEmpty()
    }

    private fun loadCreditState() {
        val savedCredits = prefs.getInt(KEY_CREDITS, 2400)
        val savedUsed = prefs.getInt(KEY_USED_TODAY, 0)
        val savedStreak = prefs.getInt(KEY_STREAK, 1)
        val lastClaimDay = prefs.getInt(KEY_LAST_CLAIM_DAY, -1)
        val today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

        _credits.value = savedCredits
        _usedToday.value = savedUsed
        _streakDays.value = savedStreak
        _claimedBonusToday.value = (lastClaimDay == today)
    }

    private fun checkAndResetDailyCredits() {
        val lastResetDay = prefs.getInt(KEY_LAST_RESET_DAY, -1)
        val today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)

        if (lastResetDay != today) {
            val current = _credits.value
            val newBalance = if (current < 2400) 2400 else current
            _credits.value = newBalance
            _usedToday.value = 0
            _claimedBonusToday.value = false

            prefs.edit()
                .putInt(KEY_LAST_RESET_DAY, today)
                .putInt(KEY_CREDITS, newBalance)
                .putInt(KEY_USED_TODAY, 0)
                .apply()

            CoroutineScope(Dispatchers.IO).launch {
                creditDao.insertTransaction(
                    CreditTransaction(
                        action = "daily_reset",
                        creditsChange = if (current < 2400) (2400 - current) else 0,
                        balanceAfter = newBalance,
                        note = "Daily allowance refilled to 2,400 credits"
                    )
                )
            }
        }
    }

    fun claimDailyBonus(): Boolean {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        if (_claimedBonusToday.value) return false

        val bonus = 50 + (_streakDays.value * 10)
        val newBalance = _credits.value + bonus
        val newStreak = _streakDays.value + 1

        _credits.value = newBalance
        _streakDays.value = newStreak
        _claimedBonusToday.value = true

        prefs.edit()
            .putInt(KEY_CREDITS, newBalance)
            .putInt(KEY_STREAK, newStreak)
            .putInt(KEY_LAST_CLAIM_DAY, today)
            .apply()

        CoroutineScope(Dispatchers.IO).launch {
            creditDao.insertTransaction(
                CreditTransaction(
                    action = "daily_bonus",
                    creditsChange = bonus,
                    balanceAfter = newBalance,
                    note = "Day ${_streakDays.value} daily streak reward claimed (+${bonus} cr)"
                )
            )
        }
        return true
    }

    fun redeemCoupon(code: String): Result<Int> {
        val cleanCode = code.trim().uppercase()
        val bonus = when (cleanCode) {
            "WELCOME50" -> 500
            "FRIEND100" -> 1000
            "SAVE200" -> 2000
            "CYBER2026" -> 2500
            "FUTUREAI" -> 3000
            "NEXUSVIP" -> 5000
            else -> return Result.failure(IllegalArgumentException("Invalid or expired coupon code"))
        }

        val redeemedSet = prefs.getStringSet(KEY_REDEEMED_COUPONS, mutableSetOf()) ?: mutableSetOf()
        if (redeemedSet.contains(cleanCode)) {
            return Result.failure(IllegalStateException("Coupon '$cleanCode' has already been redeemed!"))
        }

        val newSet = redeemedSet.toMutableSet().apply { add(cleanCode) }
        val newBalance = _credits.value + bonus
        _credits.value = newBalance

        prefs.edit()
            .putInt(KEY_CREDITS, newBalance)
            .putStringSet(KEY_REDEEMED_COUPONS, newSet)
            .apply()

        CoroutineScope(Dispatchers.IO).launch {
            creditDao.insertTransaction(
                CreditTransaction(
                    action = "coupon_redeem",
                    creditsChange = bonus,
                    balanceAfter = newBalance,
                    note = "Redeemed coupon '$cleanCode' (+${bonus} cr)"
                )
            )
        }
        return Result.success(bonus)
    }

    fun purchasePackage(packageName: String, creditsAmount: Int): Boolean {
        val newBalance = _credits.value + creditsAmount
        _credits.value = newBalance

        prefs.edit().putInt(KEY_CREDITS, newBalance).apply()

        CoroutineScope(Dispatchers.IO).launch {
            creditDao.insertTransaction(
                CreditTransaction(
                    action = "purchase",
                    creditsChange = creditsAmount,
                    balanceAfter = newBalance,
                    note = "Purchased $packageName package (+${creditsAmount} cr)"
                )
            )
        }
        return true
    }

    fun calculateCredits(quality: String): Int {
        return when (quality.lowercase()) {
            "480p" -> 3
            "720p" -> 9
            "1080p" -> 15
            "4k" -> 25
            else -> 9
        }
    }

    suspend fun toggleFavorite(id: String, currentFav: Boolean) {
        videoDao.setFavorite(id, !currentFav)
    }

    suspend fun incrementLikes(id: String) {
        videoDao.incrementLikes(id)
    }

    suspend fun deleteVideo(id: String) {
        videoDao.deleteVideoById(id)
    }

    suspend fun generateCreation(
        title: String,
        prompt: String,
        videoType: String,
        style: String,
        quality: String,
        fps: Int,
        duration: Int,
        cameraMotion: String,
        aspectRatio: String,
        inputImageRes: Int? = null,
        visualPreset: String = "CYBER_CITY",
        onProgress: (Int, String) -> Unit
    ): VideoEntity {
        val cost = calculateCredits(quality)
        require(_credits.value >= cost) { "Insufficient credits. Need $cost credits." }

        val newCredits = _credits.value - cost
        _credits.value = newCredits
        val newUsed = _usedToday.value + cost
        _usedToday.value = newUsed

        prefs.edit()
            .putInt(KEY_CREDITS, newCredits)
            .putInt(KEY_USED_TODAY, newUsed)
            .apply()

        creditDao.insertTransaction(
            CreditTransaction(
                action = if (videoType == "text_to_image") "image_generation" else "video_generation",
                creditsChange = -cost,
                balanceAfter = newCredits,
                note = "Generated $quality $style $videoType (-$cost cr)"
            )
        )

        onProgress(15, "Tokenizing & Encoding Neural Prompts...")
        delay(650)
        onProgress(35, "Latent Motion Diffusion & Physics Sampling...")
        delay(750)
        onProgress(65, "Frame Interpolation & $quality Super-Resolution AI...")
        delay(800)
        onProgress(88, "Neural Audio Sync & Spectral Color Grading...")
        delay(600)
        onProgress(100, "Neural Render Complete!")
        delay(350)

        val newVideo = VideoEntity(
            id = UUID.randomUUID().toString(),
            title = if (title.isNotBlank()) title else "Render #${System.currentTimeMillis() % 10000}",
            description = "AI generated with $style engine at $quality resolution ($fps FPS)",
            videoType = videoType,
            prompt = prompt,
            inputImageUrl = inputImageRes?.toString(),
            duration = duration,
            quality = quality,
            fps = fps,
            creditsUsed = cost,
            status = "completed",
            style = style,
            cameraMotion = cameraMotion,
            aspectRatio = aspectRatio,
            visualEffectPreset = visualPreset,
            createdAt = System.currentTimeMillis()
        )

        videoDao.insertVideo(newVideo)
        return newVideo
    }

    fun getTemplates(): List<Template> {
        return listOf(
            Template(
                id = "tpl_cyber_city",
                title = "Neo-Tokyo Rain Flight",
                description = "High-speed aerial camera swoop between neon skyscrapers with holographic billboards and flying sky-cabs.",
                prompt = "Cinematic flight through rainy Neo-Tokyo cyberpunk metropolis, neon reflections, glowing cyan holograms, flying vehicles passing by, 8k hyper-realistic",
                category = "Cyberpunk",
                style = "Cyberpunk",
                quality = "1080p",
                fps = 60,
                duration = 30,
                cameraMotion = "Drone Flyby",
                aspectRatio = "16:9",
                drawableRes = R.drawable.banner_cyber_city_1792072581849,
                usesCount = 14200,
                likesCount = 3890,
                visualPreset = "CYBER_CITY"
            ),
            Template(
                id = "tpl_alien_dragon",
                title = "Bioluminescent Leviathan",
                description = "Mythical glowing creature perched in an ethereal alien forest breathing glowing mist.",
                prompt = "Bioluminescent dragon in alien mystical rainforest, glowing cyan spores floating, moonlight beams, volumetric cinematic smoke, Unreal Engine 5 render",
                category = "Sci-Fi VFX",
                style = "Unreal Engine 5",
                quality = "4k",
                fps = 30,
                duration = 15,
                cameraMotion = "Orbit 360",
                aspectRatio = "16:9",
                drawableRes = R.drawable.alien_biolum_vfx_1792072606950,
                usesCount = 8900,
                likesCount = 2740,
                visualPreset = "DRAGON_VFX"
            ),
            Template(
                id = "tpl_android_awakening",
                title = "Cyborg Neural Awakening",
                description = "Close-up portrait of female android with glowing neural circuits scanning the environment.",
                prompt = "Close up of futuristic female cyborg face, glowing cyan circuitry pulsing, camera zoom in, rainy glass reflections, photorealistic 8k cybernetics",
                category = "Cinematic",
                style = "Hyper-Realistic",
                quality = "1080p",
                fps = 30,
                duration = 20,
                cameraMotion = "Push In / Zoom",
                aspectRatio = "1:1",
                drawableRes = R.drawable.cyber_android_portrait_1792072607019,
                usesCount = 11200,
                likesCount = 4120,
                visualPreset = "CYBORG_SCAN"
            ),
            Template(
                id = "tpl_warp_drive",
                title = "Interstellar Warp Jump",
                description = "Starship jumping into lightspeed hyperspace with cosmic particle streaks and nebula blast.",
                prompt = "Hyperspace warp drive activation, streaks of light, cosmic nebula explosion, 60fps ultra smooth cinematic space opera",
                category = "Sci-Fi VFX",
                style = "Cinematic",
                quality = "4k",
                fps = 60,
                duration = 15,
                cameraMotion = "Push In / Zoom",
                aspectRatio = "16:9",
                drawableRes = R.drawable.banner_cyber_city_1792072581849,
                usesCount = 6700,
                likesCount = 1940,
                visualPreset = "NEBULA_WARP"
            )
        )
    }

    private fun initializeInitialCreationsIfEmpty() {
        CoroutineScope(Dispatchers.IO).launch {
            if (videoDao.getVideoCount() == 0) {
                videoDao.insertVideo(
                    VideoEntity(
                        id = "showcase_1",
                        title = "Neo-Tokyo Cyber Chase",
                        description = "Futuristic speeder race through rainy skyscrapers with neon reflections.",
                        videoType = "text_to_video",
                        prompt = "Cyberpunk hovercar chase through high-rise canyon in Neo Tokyo, neon reflections, rain, 8k resolution",
                        quality = "1080p",
                        fps = 60,
                        duration = 30,
                        creditsUsed = 15,
                        views = 284,
                        likes = 42,
                        isFavorite = true,
                        style = "Cyberpunk",
                        cameraMotion = "Drone Flyby",
                        aspectRatio = "16:9",
                        visualEffectPreset = "CYBER_CITY",
                        createdAt = System.currentTimeMillis() - 3600000 * 2
                    )
                )
                videoDao.insertVideo(
                    VideoEntity(
                        id = "showcase_2",
                        title = "Bioluminescent Forest Awakening",
                        description = "Glowing mythical creature awakening under the alien moon.",
                        videoType = "text_to_video",
                        prompt = "Glowing cyan dragon resting on bioluminescent tree branch, floating ethereal particles",
                        quality = "4k",
                        fps = 30,
                        duration = 15,
                        creditsUsed = 25,
                        views = 156,
                        likes = 29,
                        isFavorite = false,
                        style = "Unreal Engine 5",
                        cameraMotion = "Orbit 360",
                        aspectRatio = "16:9",
                        visualEffectPreset = "DRAGON_VFX",
                        createdAt = System.currentTimeMillis() - 3600000 * 5
                    )
                )
                videoDao.insertVideo(
                    VideoEntity(
                        id = "showcase_3",
                        title = "Neural Android Scan",
                        description = "Cybernetic headshot portrait scanning surroundings with neural telemetry.",
                        videoType = "text_to_image",
                        prompt = "Futuristic cybernetic woman with glowing cyan circuit implants, cyberpunk city rain background",
                        quality = "1080p",
                        fps = 30,
                        duration = 10,
                        creditsUsed = 9,
                        views = 98,
                        likes = 18,
                        isFavorite = true,
                        style = "Hyper-Realistic",
                        cameraMotion = "Push In / Zoom",
                        aspectRatio = "1:1",
                        visualEffectPreset = "CYBORG_SCAN",
                        createdAt = System.currentTimeMillis() - 3600000 * 12
                    )
                )

                if (creditDao.getTransactionCount() == 0) {
                    creditDao.insertTransaction(
                        CreditTransaction(
                            action = "initial_grant",
                            creditsChange = 2400,
                            balanceAfter = 2400,
                            note = "Welcome to Nexus AI! 2,400 daily credits granted."
                        )
                    )
                }
            }
        }
    }

    companion object {
        private const val KEY_CREDITS = "user_credits"
        private const val KEY_USED_TODAY = "credits_used_today"
        private const val KEY_STREAK = "streak_days"
        private const val KEY_LAST_RESET_DAY = "last_reset_day"
        private const val KEY_LAST_CLAIM_DAY = "last_claim_day"
        private const val KEY_REDEEMED_COUPONS = "redeemed_coupons_set"
    }
}
