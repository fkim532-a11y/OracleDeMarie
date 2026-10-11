package com.kim.oraclemarie.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MarieColorScheme = darkColorScheme(
    primary = SacredGold,
    onPrimary = DarkNavy,
    primaryContainer = MarianBlue,
    onPrimaryContainer = SoftWhite,
    secondary = GoldLight,
    onSecondary = DarkNavy,
    secondaryContainer = NavyLight,
    onSecondaryContainer = SoftWhite,
    background = DarkNavy,
    onBackground = SoftWhite,
    surface = MarianBlue,
    onSurface = SoftWhite,
    surfaceVariant = Color(0xFF1A2B5A),
    onSurfaceVariant = SoftMist,
    tertiary = MysticPurple,
    onTertiary = SoftWhite,
    error = Color(0xFFCF6679),
    onError = DarkNavy
)

@Composable
fun MarieTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MarieColorScheme,
        typography = Typography,
        content = content
    )
}
