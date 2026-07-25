package com.echosmart.flashlabs.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppTheme {
    DARK_OLED,
    LIGHT_PRO,
    CYBERPUNK,
    RETRO_AMBER
}

private val DarkOledColorScheme = darkColorScheme(
    primary = DarkOledPrimary,
    secondary = DarkOledSecondary,
    background = DarkOledBackground,
    surface = DarkOledSurface,
    onPrimary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightProColorScheme = lightColorScheme(
    primary = LightProPrimary,
    secondary = LightProSecondary,
    background = LightProBackground,
    surface = LightProSurface,
    onPrimary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

private val CyberpunkColorScheme = darkColorScheme(
    primary = CyberpunkPrimary,
    secondary = CyberpunkSecondary,
    background = CyberpunkBackground,
    surface = CyberpunkSurface,
    onPrimary = Color.White,
    onBackground = CyberpunkSecondary,
    onSurface = CyberpunkPrimary
)

private val RetroAmberColorScheme = darkColorScheme(
    primary = RetroAmberPrimary,
    secondary = RetroAmberSecondary,
    background = RetroAmberBackground,
    surface = RetroAmberSurface,
    onPrimary = Color.Black,
    onBackground = RetroAmberPrimary,
    onSurface = RetroAmberPrimary
)

@Composable
fun FlashLabsTheme(
    theme: AppTheme = AppTheme.DARK_OLED,
    content: @Composable () -> Unit
) {
    val colors = when (theme) {
        AppTheme.DARK_OLED -> DarkOledColorScheme
        AppTheme.LIGHT_PRO -> LightProColorScheme
        AppTheme.CYBERPUNK -> CyberpunkColorScheme
        AppTheme.RETRO_AMBER -> RetroAmberColorScheme
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
