package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = KisanGreenPrimaryDark,
    onPrimary = KisanGreenOnPrimaryDark,
    primaryContainer = KisanGreenContainerDark,
    onPrimaryContainer = KisanGreenOnContainerDark,
    secondary = KisanGoldSecondaryDark,
    onSecondary = KisanGoldOnSecondaryDark,
    secondaryContainer = KisanGoldContainerDark,
    onSecondaryContainer = KisanGoldOnContainerDark,
    background = KisanBackgroundDark,
    onBackground = KisanOnBackgroundDark,
    surface = KisanSurfaceDark,
    onSurface = KisanOnSurfaceDark,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = KisanGreenPrimary,
    onPrimary = KisanGreenOnPrimary,
    primaryContainer = KisanGreenContainer,
    onPrimaryContainer = KisanGreenOnContainer,
    secondary = KisanGoldSecondary,
    onSecondary = KisanGoldOnSecondary,
    secondaryContainer = KisanGoldContainer,
    onSecondaryContainer = KisanGoldOnContainer,
    tertiary = KisanEarthTertiary,
    tertiaryContainer = KisanEarthContainer,
    onTertiaryContainer = KisanEarthOnContainer,
    background = KisanBackground,
    onBackground = KisanOnBackground,
    surface = KisanSurface,
    onSurface = KisanOnSurface,
    surfaceVariant = KisanSurfaceVariant,
    onSurfaceVariant = KisanOnSurfaceVariant,
    outline = KisanOutline,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Use our curated agriculture colors as default, dynamic color optional
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
