package com.zeyadmamoun.korainsight.ui.theme

import android.app.Activity
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
    primary = BluePrimaryDark,
    onPrimary = Color(0xFF0F1E40),
    primaryContainer = BluePrimaryContainerDark,
    onPrimaryContainer = OnBluePrimaryContainerDark,
    secondary = CoralSecondaryDark,
    onSecondary = Color(0xFF400A13),
    secondaryContainer = CoralSecondaryContainerDark,
    onSecondaryContainer = OnCoralSecondaryContainerDark,
    background = AppBackgroundDark,
    onBackground = DarkOnSurfaceDark,
    surface = CardSurfaceDark,
    onSurface = DarkOnSurfaceDark,
    surfaceVariant = PillSurfaceVariantDark,
    onSurfaceVariant = SlateOnSurfaceVariantDark,
    outline = OutlineSubtleDark
)

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = Color.White,
    primaryContainer = BluePrimaryContainer,
    onPrimaryContainer = OnBluePrimaryContainer,
    secondary = CoralSecondary,
    onSecondary = Color.White,
    secondaryContainer = CoralSecondaryContainer,
    onSecondaryContainer = OnCoralSecondaryContainer,
    background = AppBackground,
    onBackground = DarkOnSurface,
    surface = CardSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = PillSurfaceVariant,
    onSurfaceVariant = SlateOnSurfaceVariant,
    outline = OutlineSubtle
)

@Composable
fun KoraInsightTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Default to false so the custom brand colors are displayed on Android 12+
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