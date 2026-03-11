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

private val LightColorScheme = lightColorScheme(
    primary = OrangePrimary,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDCC2),
    onPrimaryContainer = Color(0xFF4A1800),
    secondary = OrangeDark,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDCC2),
    onSecondaryContainer = Color(0xFF4A1800),
    tertiary = Color(0xFF8B5E00),
    background = Color(0xFFFFFBF8),
    onBackground = Color(0xFF1A1A1A),
    surface = Color(0xFFF5F0EB),
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFEDE8E3),
    onSurfaceVariant = Color(0xFF4A4540),
    error = Color(0xFFBA1A1A),
    outline = Color(0xFFCCC5BE)
)

@Composable
fun OrangeModLoaderTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography(),
        content = content
    )
}
