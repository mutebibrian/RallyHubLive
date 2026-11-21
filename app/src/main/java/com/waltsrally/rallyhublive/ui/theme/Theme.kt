package com.waltsrally.rallyhublive.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Palette ───────────────────────────────────────────────────────────────────
val RallyBlack    = Color(0xFF0A0A0A)
val RallyDark     = Color(0xFF161616)
val RallyCard     = Color(0xFF1E1E1E)
val RallyBorder   = Color(0xFF2C2C2E)
val RallyGold     = Color(0xFFF5C842)
val RallyGreen    = Color(0xFF34C759)
val RallyRed      = Color(0xFFFF3B30)
val RallyBlue     = Color(0xFF007AFF)
val RallyGray     = Color(0xFF8E8E93)
val RallyWhite    = Color(0xFFF2F2F7)
val RallySubtext  = Color(0xFF6D6D72)

private val DarkColorScheme = darkColorScheme(
    primary          = RallyGold,
    onPrimary        = RallyBlack,
    primaryContainer = Color(0xFF3A3000),
    background       = RallyBlack,
    surface          = RallyDark,
    surfaceVariant   = RallyCard,
    onBackground     = RallyWhite,
    onSurface        = RallyWhite,
    onSurfaceVariant = RallyGray,
    outline          = RallyBorder,
    error            = RallyRed,
    secondary        = RallyBlue,
    tertiary         = RallyGreen
)

@Composable
fun RallyHubTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography  = Typography(),
        content     = content
    )
}
