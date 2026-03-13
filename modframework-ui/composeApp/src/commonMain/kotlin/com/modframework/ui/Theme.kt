package com.modframework.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val MangoPrimary = Color(0xFFFFB300)
val MangoLight = Color(0xFFFFD54F)
val MangoDark = Color(0xFFF57F17)
val MangoContainer = Color(0xFF2D1E00)
val LeafGreen = Color(0xFF7CB342)
val LeafDark = Color(0xFF558B2F)

private val DarkColorScheme = darkColorScheme(
    primary = MangoPrimary,
    onPrimary = Color(0xFF1A1000),
    primaryContainer = MangoContainer,
    onPrimaryContainer = MangoLight,
    secondary = LeafGreen,
    onSecondary = Color(0xFF1A1000),
    secondaryContainer = Color(0xFF1B2E00),
    onSecondaryContainer = Color(0xFFB7E07A),
    tertiary = MangoDark,
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
    primary = LeafDark,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFDCEDC8),
    onPrimaryContainer = Color(0xFF1B2E00),
    secondary = MangoDark,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFE082),
    onSecondaryContainer = Color(0xFF2D1E00),
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
fun MangoLoaderTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography(),
        content = content
    )
}