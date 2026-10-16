package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Futuristic Cyberpunk & Sci-Fi Palette
val CyberBackground = Color(0xFF07090E)
val CyberSurface = Color(0xFF0D121D)
val CyberSurfaceVariant = Color(0xFF141C2E)
val CyberCardBorder = Color(0xFF1E293B)

// Neon Primary & Accent Glows
val NeonCyan = Color(0xFF00F2FE)
val NeonCyanGlow = Color(0x3300F2FE)
val ElectricViolet = Color(0xFF9D4EDD)
val ElectricPurple = Color(0xFF7928CA)
val CyberPink = Color(0xFFFF007F)
val NeonGreen = Color(0xFF00FF87)
val HologramBlue = Color(0xFF4FACFE)
val WarningAmber = Color(0xFFFFB703)
val DangerCoral = Color(0xFFFF3366)

// Text Colors
val TextPrimary = Color(0xFFF1F5F9)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)
val TextCyan = Color(0xFF38BDF8)

// Theme Accents for Customizable Cyber Vibe
enum class CyberThemeMode(val displayName: String, val primary: Color, val secondary: Color) {
    CYBER_NEON("Cyber Neon", NeonCyan, ElectricViolet),
    MATRIX_PULSE("Matrix Pulse", NeonGreen, NeonCyan),
    SOLAR_FLARE("Solar Flare", CyberPink, WarningAmber)
}
