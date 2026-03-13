package com.photoapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFF8A3D),
    onPrimary = Color(0xFF1E1308),
    secondary = Color(0xFF8FB0FF),
    onSecondary = Color(0xFF101A33),
    background = Color(0xFF0F1116),
    onBackground = Color(0xFFE8EBF2),
    surface = Color(0xFF1A1E27),
    onSurface = Color(0xFFE8EBF2),
    surfaceVariant = Color(0xFF262D3A),
    onSurfaceVariant = Color(0xFFB6C0D4)
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF9F4A13),
    onPrimary = Color.White,
    secondary = Color(0xFF3A5A9B),
    onSecondary = Color.White,
    background = Color(0xFFF7F8FC),
    onBackground = Color(0xFF1A1E27),
    surface = Color.White,
    onSurface = Color(0xFF1A1E27),
    surfaceVariant = Color(0xFFE7EAF2),
    onSurfaceVariant = Color(0xFF4E586E)
)

@Composable
fun PhotoAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
