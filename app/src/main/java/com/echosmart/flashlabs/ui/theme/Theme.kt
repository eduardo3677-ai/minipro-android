package com.echosmart.flashlabs.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.echosmart.flashlabs.R

enum class AppTheme {
    DARK_OLED,
    LIGHT_PRO,
    CYBERPUNK,
    RETRO_AMBER
}

@Composable
fun FlashLabsTheme(
    theme: AppTheme = AppTheme.DARK_OLED,
    content: @Composable () -> Unit
) {
    val darkOledColorScheme = darkColorScheme(
        primary = colorResource(id = R.color.dark_oled_primary),
        secondary = colorResource(id = R.color.dark_oled_secondary),
        background = colorResource(id = R.color.dark_oled_background),
        surface = colorResource(id = R.color.dark_oled_surface),
        onPrimary = Color.Black,
        onBackground = Color.White,
        onSurface = Color.White
    )

    val lightProColorScheme = lightColorScheme(
        primary = colorResource(id = R.color.light_pro_primary),
        secondary = colorResource(id = R.color.light_pro_secondary),
        background = colorResource(id = R.color.light_pro_background),
        surface = colorResource(id = R.color.light_pro_surface),
        onPrimary = Color.White,
        onBackground = Color.Black,
        onSurface = Color.Black
    )

    val cyberpunkColorScheme = darkColorScheme(
        primary = colorResource(id = R.color.cyberpunk_primary),
        secondary = colorResource(id = R.color.cyberpunk_secondary),
        background = colorResource(id = R.color.cyberpunk_background),
        surface = colorResource(id = R.color.cyberpunk_surface),
        onPrimary = Color.White,
        onBackground = colorResource(id = R.color.cyberpunk_secondary),
        onSurface = colorResource(id = R.color.cyberpunk_primary)
    )

    val retroAmberColorScheme = darkColorScheme(
        primary = colorResource(id = R.color.retro_amber_primary),
        secondary = colorResource(id = R.color.retro_amber_secondary),
        background = colorResource(id = R.color.retro_amber_background),
        surface = colorResource(id = R.color.retro_amber_surface),
        onPrimary = Color.Black,
        onBackground = colorResource(id = R.color.retro_amber_primary),
        onSurface = colorResource(id = R.color.retro_amber_primary)
    )

    val colors = when (theme) {
        AppTheme.DARK_OLED -> darkOledColorScheme
        AppTheme.LIGHT_PRO -> lightProColorScheme
        AppTheme.CYBERPUNK -> cyberpunkColorScheme
        AppTheme.RETRO_AMBER -> retroAmberColorScheme
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
