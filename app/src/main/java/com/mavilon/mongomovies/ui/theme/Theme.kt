package com.mavilon.mongomovies.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AccentCyan,
    secondary = AccentBlue,
    tertiary = RatingOrange,
    background = BackgroundPrimary,
    surface = SurfaceColor,
    onPrimary = TextPrimary,
    onSecondary = TextPrimary,
    onTertiary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = ErrorRed,
    onError = TextPrimary
)

@Composable
fun MongomoviesTheme(
    darkTheme: Boolean = true, // Always dark theme for this app
    dynamicColor: Boolean = false, // Disable dynamic color to maintain design
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}