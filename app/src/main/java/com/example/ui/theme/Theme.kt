package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PomoPrimaryDark,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF781B12),
    onPrimaryContainer = Color(0xFFFFDAD4),
    secondary = PomoAccentGold,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF452B00),
    onSecondaryContainer = Color(0xFFFFDEAB),
    tertiary = BreakGreenDark,
    onTertiary = Color.Black,
    background = WarmBgDark,
    onBackground = TextPrimaryDark,
    surface = WarmCardDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = Slate800,
    onSurfaceVariant = TextSecondaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = PomoPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD4),
    onPrimaryContainer = Color(0xFF410001),
    secondary = PomoSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE0CF),
    onSecondaryContainer = Color(0xFF331200),
    tertiary = BreakGreen,
    onTertiary = Color.White,
    background = WarmBgLight,
    onBackground = TextPrimaryLight,
    surface = WarmCardLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Slate100,
    onSurfaceVariant = TextSecondaryLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep bespoke brand identity for Pomodoro Focus
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
