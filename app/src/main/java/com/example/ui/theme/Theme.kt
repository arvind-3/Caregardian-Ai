package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = CareBluePrimary,
    onPrimary = Color.White,
    primaryContainer = CareBlueLight,
    onPrimaryContainer = CareBlueDark,
    secondary = CareGreenSuccess,
    onSecondary = Color.White,
    secondaryContainer = CareGreenLight,
    onSecondaryContainer = Color(0xFF064E3B),
    tertiary = CarePurple,
    onTertiary = Color.White,
    tertiaryContainer = CarePurpleLight,
    onTertiaryContainer = Color(0xFF4C1D95),
    error = CareRedAlert,
    onError = Color.White,
    errorContainer = CareRedLight,
    onErrorContainer = Color(0xFF7F1D1D),
    background = CareBackground,
    onBackground = CareTextPrimary,
    surface = CareSurface,
    onSurface = CareTextPrimary,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = CareTextSecondary,
    outline = CareBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF60A5FA),
    onPrimary = Color(0xFF0F172A),
    primaryContainer = CareBlueDark,
    onPrimaryContainer = CareBlueLight,
    secondary = Color(0xFF34D399),
    onSecondary = Color(0xFF0F172A),
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC),
    error = Color(0xFFF87171)
)

@Composable
fun CareGuardianTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
