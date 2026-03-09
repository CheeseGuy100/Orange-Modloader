package com.modframework.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val OrangePrimary = Color(0xFFFF6B00)
val OrangeLight = Color(0xFFFF9A4D)
val OrangeDark = Color(0xFFCC5500)
val OrangeContainer = Color(0xFF2D1800)
val OrangeGlow = Color(0xFFFF6B00).copy(alpha = 0.15f)

private val DarkColorScheme = darkColorScheme(
    primary = OrangePrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = OrangeContainer,
    onPrimaryContainer = OrangeLight,
    secondary = OrangeLight,
    onSecondary = Color(0xFF1A0A00),
    secondaryContainer = Color(0xFF3D1F00),
    onSecondaryContainer = Color(0xFFFFD0A8),
    tertiary = Color(0xFFFFD166),
    background = Color(0xFF0E0E0E),
    onBackground = Color(0xFFF0EDE8),
    surface = Color(0xFF1A1A1A),
    onSurface = Color(0xFFF0EDE8),
    surfaceVariant = Color(0xFF252525),
    onSurfaceVariant = Color(0xFF9E9E9E),
    error = Color(0xFFFF5252),
    outline = Color(0xFF333333)
)

@Composable
fun OrangeModLoaderTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography(),
        content = content
    )
}