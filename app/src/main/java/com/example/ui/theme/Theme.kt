package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = GameOrangePrimary,
  onPrimary = Color.White,
  secondary = GameGoldenYellow,
  onSecondary = Color.Black,
  tertiary = GameGreenPlay,
  background = GameDarkBg,
  surface = GameDarkSurface,
  surfaceVariant = GameDarkWoodTray,
  onBackground = Color(0xFFFBE9E7),
  onSurface = Color(0xFFFBE9E7)
)

private val LightColorScheme = lightColorScheme(
  primary = GameOrangePrimary,
  onPrimary = Color.White,
  secondary = GameGoldenYellow,
  onSecondary = Color(0xFF3E2723),
  tertiary = GameGreenPlay,
  background = GameCreamBg,
  surface = GameCreamCard,
  surfaceVariant = GameWoodTrayBg,
  onBackground = Color(0xFF3E2723),
  onSurface = Color(0xFF3E2723)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep consistent game aesthetics
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
