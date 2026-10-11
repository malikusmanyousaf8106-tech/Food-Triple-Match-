package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.dialogs.RulesDialog
import com.example.ui.dialogs.SettingsDialog
import com.example.ui.screens.GameScreen
import com.example.ui.screens.LevelSelectScreen
import com.example.ui.screens.MenuScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.ActiveDialog
import com.example.viewmodel.AppScreen
import com.example.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background
        ) {
          FoodMatchApp()
        }
      }
    }
  }
}

@Composable
fun FoodMatchApp(
  viewModel: GameViewModel = viewModel()
) {
  val state by viewModel.uiState.collectAsState()

  when (state.currentScreen) {
    AppScreen.MENU -> {
      MenuScreen(viewModel = viewModel)
    }
    AppScreen.LEVEL_SELECT -> {
      LevelSelectScreen(viewModel = viewModel)
    }
    AppScreen.PLAYING -> {
      GameScreen(viewModel = viewModel)
    }
  }

  // Global Dialogs accessible from Menu / Everywhere
  when (state.activeDialog) {
    ActiveDialog.SETTINGS -> {
      SettingsDialog(
        soundEnabled = state.soundEnabled,
        musicEnabled = state.musicEnabled,
        hapticsEnabled = state.hapticsEnabled,
        onToggleSound = { viewModel.toggleSound() },
        onToggleMusic = { viewModel.toggleMusic() },
        onToggleHaptics = { viewModel.toggleHaptics() },
        onResetProgress = { viewModel.resetAllProgress() },
        onDismiss = { viewModel.showDialog(ActiveDialog.NONE) }
      )
    }
    ActiveDialog.RULES -> {
      RulesDialog(
        onDismiss = { viewModel.showDialog(ActiveDialog.NONE) }
      )
    }
    else -> {}
  }
}
