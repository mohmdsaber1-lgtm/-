package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val DarkColorScheme = darkColorScheme(
    primary = PatrolGold,
    secondary = PatrolAmber,
    tertiary = PatrolCrimson,
    background = PatrolDarkCharcoal,
    surface = PatrolCardDark,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = PatrolGold,
    secondary = PatrolAmber,
    tertiary = PatrolCrimson,
    background = PatrolLightBg,
    surface = PatrolSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = PatrolTextPrimary,
    onSurface = PatrolTextPrimary
)

@Composable
fun PatrolMaintenanceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

