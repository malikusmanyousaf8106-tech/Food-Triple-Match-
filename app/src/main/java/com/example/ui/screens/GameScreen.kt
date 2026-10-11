package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Tile
import com.example.viewmodel.ActiveDialog
import com.example.ui.components.CollectionTrayView
import com.example.ui.components.ConfettiOverlay
import com.example.ui.components.FoodTileView
import com.example.ui.components.GameOverDialog
import com.example.ui.components.PauseDialog
import com.example.ui.components.PowerUpBar
import com.example.ui.components.ShopDialog
import com.example.ui.components.TopGameBar
import com.example.ui.components.VictoryDialog
import com.example.ui.theme.GameCreamBg
import com.example.viewmodel.AppScreen
import com.example.viewmodel.GameViewModel

@Composable
fun GameScreen(
  viewModel: GameViewModel,
  modifier: Modifier = Modifier
) {
  val state by viewModel.uiState.collectAsState()

  // Handle hardware / gesture back button
  BackHandler {
    viewModel.onPause()
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            GameCreamBg,
            Color(0xFFFFF0E0),
            Color(0xFFFFE8D6)
          )
        )
      )
      .statusBarsPadding()
      .navigationBarsPadding()
      .testTag("game_screen")
  ) {
    Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Top Bar
      TopGameBar(
        levelNumber = state.levelNumber,
        score = state.score,
        targetScore = state.targetScore,
        remainingTiles = state.remainingTiles,
        totalTiles = state.totalTilesInLevel,
        comboCount = state.comboCount,
        soundEnabled = state.soundEnabled,
        onHomeClick = { viewModel.navigateTo(AppScreen.MENU) },
        onPauseClick = { viewModel.onPause() },
        onSoundToggle = { viewModel.toggleSound() }
      )

      // Tutorial badge on Level 1
      if (state.levelNumber == 1 && state.remainingTiles > 10) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFFFFF9C4),
          modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 2.dp)
            .border(1.dp, Color(0xFFFFE082), RoundedCornerShape(12.dp))
        ) {
          Text(
            text = "💡 Tap matching food items! Match 3 identical foods to clear tray slots.",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = Color(0xFF5D4037),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            textAlign = TextAlign.Center
          )
        }
      }

      // Main Game Board (responsive 3D grid layout)
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
      ) {
        BoxWithConstraints(
          modifier = Modifier
            .fillMaxSize()
            .testTag("game_board"),
          contentAlignment = Alignment.Center
        ) {
          val boardWidth = maxWidth
          val boardHeight = maxHeight

          // Responsive tile size based on screen width
          val tileSize = when {
            boardWidth < 340.dp -> 48.dp
            boardWidth < 380.dp -> 54.dp
            boardWidth < 430.dp -> 60.dp
            else -> 64.dp
          }

          val stepX = tileSize * 0.94f
          val stepY = tileSize * 0.94f

          // Sort tiles by layer ascending so higher layers naturally render on top
          val sortedTiles = state.boardTiles.sortedWith(
            compareBy<Tile> { it.layer }.thenBy { it.gridY }.thenBy { it.gridX }
          )

          sortedTiles.forEach { tile ->
            val isHinted = tile.id == state.hintedTileId
            val isMatching = state.matchingTileIds.contains(tile.id)

            val xPos = (tile.gridX * stepX.value).dp
            val yPos = (tile.gridY * stepY.value).dp

            FoodTileView(
              tile = tile,
              tileSize = tileSize,
              isHinted = isHinted,
              isMatching = isMatching,
              modifier = Modifier.offset(x = xPos, y = yPos),
              onClick = { viewModel.onTileClick(tile.id) }
            )
          }
        }
      }

      // Collection Tray (Exactly 6 slots in 2 rows of 3)
      CollectionTrayView(
        slots = state.traySlots,
        matchingTileIds = state.matchingTileIds
      )

      // Power-Up Controls
      PowerUpBar(
        undoCount = state.undoCount,
        shuffleCount = state.shuffleCount,
        hintCount = state.hintCount,
        coins = state.coins,
        onUndoClick = { viewModel.onUndo() },
        onShuffleClick = { viewModel.onShuffle() },
        onHintClick = { viewModel.onHint() }
      )

      Spacer(modifier = Modifier.height(4.dp))
    }

    // Active Dialogs & Overlays
    when (state.activeDialog) {
      ActiveDialog.LEVEL_COMPLETE -> {
        ConfettiOverlay()
        VictoryDialog(
          levelNumber = state.levelNumber,
          score = state.score,
          stars = state.starsEarned,
          coinsEarned = state.coinsEarned,
          onNextLevel = { viewModel.onNextLevel() },
          onReplay = { viewModel.onRestartLevel() },
          onHome = { viewModel.navigateTo(AppScreen.MENU) }
        )
      }
      ActiveDialog.GAME_OVER -> {
        GameOverDialog(
          score = state.score,
          onRetry = { viewModel.onRestartLevel() },
          onHome = { viewModel.navigateTo(AppScreen.MENU) }
        )
      }
      ActiveDialog.PAUSE -> {
        PauseDialog(
          onResume = { viewModel.onResume() },
          onRestart = { viewModel.onRestartLevel() },
          onHome = { viewModel.navigateTo(AppScreen.MENU) },
          soundEnabled = state.soundEnabled,
          hapticsEnabled = state.hapticsEnabled,
          onToggleSound = { viewModel.toggleSound() },
          onToggleHaptics = { viewModel.toggleHaptics() }
        )
      }
      ActiveDialog.SHOP -> {
        ShopDialog(
          coins = state.coins,
          onBuy = { viewModel.buyPowerUp(it) },
          onDismiss = { viewModel.showDialog(ActiveDialog.NONE) }
        )
      }
      else -> {}
    }
  }
}
