package com.example.nusamart.ui.theme

import android.app.Activity
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
    primary = TealPrimary,
    secondary = TealLight,
    tertiary = TealDark,

    background = DarkBackground,
    surface = DarkBackground,

    onPrimary = DarkBackground,
    onSecondary = DarkBackground,
    onTertiary = LightBackground,
    onBackground = LightBackground,
    onSurface = LightBackground
)

private val LightColorScheme = lightColorScheme(
    primary = TealPrimary,
    secondary = TealDark,
    tertiary = TealLight,

    background = LightBackground,
    surface = White,

    onPrimary = White,
    onSecondary = White,
    onTertiary = DarkBackground,
    onBackground = DarkBackground,
    onSurface = DarkBackground
)

@Composable
fun NusaMartTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color dimatikan (false) agar warna kustom di atas selalu diterapkan
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}