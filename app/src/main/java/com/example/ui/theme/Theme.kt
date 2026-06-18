package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AdPink,
    onPrimary = Color.White,
    secondary = AdCyan,
    onSecondary = SpaceBlack,
    tertiary = AdGold,
    background = SpaceBlack,
    onBackground = TextWhite,
    surface = DeepNavy,
    onSurface = TextWhite,
    surfaceVariant = CardBackground,
    onSurfaceVariant = TextGray,
    outline = BorderDark
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force Dark-Mode Default for AdCraft AI Premium SaaS styling
    dynamicColor: Boolean = false, // Disable dynamic colors to enforce the selected cinematic brand kit
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
