package com.waltsrally.rallyhublive.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Palette ───────────────────────────────────────────────────────────────────
// Dark surfaces (kept for explicit containerColor overrides)
val RallyBlack    = Color(0xFF1A1A1A)
val RallyDark     = Color(0xFFFFFFFF)   // top bars → white in light theme
val RallyCard     = Color(0xFFF5F5F5)   // card backgrounds
val RallyBorder   = Color(0xFFE0E0E0)
// Brand colours
val RallyGold     = Color(0xFFD4A017)   // slightly deeper gold for legibility on white
val RallyGreen    = Color(0xFF2E7D32)
val RallyRed      = Color(0xFFD32F2F)
val RallyBlue     = Color(0xFF1565C0)
val RallyGray     = Color(0xFF757575)
val RallyWhite    = Color(0xFF212121)   // text on light bg → near-black
val RallySubtext  = Color(0xFF9E9E9E)

private val LightColorScheme = lightColorScheme(
    primary          = RallyGold,
    onPrimary        = Color.White,
    primaryContainer = Color(0xFFFFF3CD),
    background       = Color(0xFFF8F8F8),
    surface          = Color.White,
    surfaceVariant   = RallyCard,
    onBackground     = Color(0xFF212121),
    onSurface        = Color(0xFF212121),
    onSurfaceVariant = RallyGray,
    outline          = RallyBorder,
    error            = RallyRed,
    secondary        = RallyBlue,
    tertiary         = RallyGreen
)

@Composable
fun RallyHubTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography  = Typography(),
        content     = content
    )
}
