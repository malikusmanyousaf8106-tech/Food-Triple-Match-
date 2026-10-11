package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.GameRepository
import com.example.engine.GameEngine
import com.example.engine.LevelGenerator
import com.example.model.FoodType
import com.example.model.Tile
import com.example.model.TraySlot
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppScreen {
  MENU,
  LEVEL_SELECT,
  PLAYING
}

enum class ActiveDialog {
  NONE,
  PAUSE,
  LEVEL_COMPLETE,
  GAME_OVER,
  SETTINGS,
  RULES,
  SHOP
}

data class GameUiState(
  val currentScreen: AppScreen = AppScreen.MENU,
  val activeDialog: ActiveDialog = ActiveDialog.NONE,
  val levelNumber: Int = 1,
  val totalTilesInLevel: Int = 15,
  val remainingTiles: Int = 15,
  val boardTiles: List<Tile> = emptyList(),
  val traySlots: List<TraySlot> = List(6) { index -> TraySlot(slotIndex = index) },
  val score: Int = 0,
  val targetScore: Int = 750,
  val comboCount: Int = 0,
  val hintedTileId: Int? = null,
  val matchingTileIds: Set<Int> = emptySet(),
  val isBusy: Boolean = false,
  val starsEarned: Int = 0,
  val coinsEarned: Int = 0,
  // Player stats
  val coins: Int = 100,
  val undoCount: Int = 3,
  val shuffleCount: Int = 3,
  val hintCount: Int = 3,
  val highestUnlockedLevel: Int = 1,
  val totalScore: Int = 0,
  // Settings
  val soundEnabled: Boolean = true,
  val musicEnabled: Boolean = true,
  val hapticsEnabled: Boolean = true
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

  private val repository = GameRepository(application)
  val soundManager = SoundManager(application)
  private val engine = GameEngine()

  private val _uiState = MutableStateFlow(GameUiState())
  val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

  init {
    loadRepositoryData()
  }

  fun loadRepositoryData() {
    val sound = repository.soundEnabled
    val haptics = repository.hapticsEnabled
    soundManager.soundEnabled = sound
    soundManager.hapticsEnabled = haptics

    _uiState.update { current ->
      current.copy(
        coins = repository.coins,
        undoCount = repository.undoCount,
        shuffleCount = repository.shuffleCount,
        hintCount = repository.hintCount,
        highestUnlockedLevel = repository.highestUnlockedLevel,
        totalScore = repository.totalScore,
        soundEnabled = sound,
        musicEnabled = repository.musicEnabled,
        hapticsEnabled = haptics
      )
    }
  }

  fun getStarsForLevel(level: Int): Int = repository.getStarsForLevel(level)
  fun getHighScoreForLevel(level: Int): Int = repository.getHighScoreForLevel(level)

  fun startLevel(levelNumber: Int) {
    val level = levelNumber.coerceIn(1, 100)
    engine.startLevel(level)
    val config = LevelGenerator.getConfigForLevel(level)

    repository.lastPlayedLevel = level
    soundManager.playButtonClick()

    _uiState.update { current ->
      current.copy(
        currentScreen = AppScreen.PLAYING,
        activeDialog = ActiveDialog.NONE,
        levelNumber = level,
        totalTilesInLevel = config.totalTiles,
        remainingTiles = config.totalTiles,
        boardTiles = engine.boardTiles,
        traySlots = engine.traySlots.toList(),
        score = 0,
        targetScore = config.targetScore,
        comboCount = 0,
        hintedTileId = null,
        matchingTileIds = emptySet(),
        isBusy = false,
        starsEarned = 0,
        coinsEarned = 0
      )
    }
  }

  fun onTileClick(tileId: Int) {
    val state = _uiState.value
    if (state.isBusy) return
    if (!engine.canSelectTile(tileId)) return

    // Clear hint if present
    _uiState.update { it.copy(hintedTileId = null) }

    soundManager.playTap()

    // Execute engine selection
    val result = engine.selectTile(tileId)
    if (!result.success) return

    val updatedTray = engine.traySlots.toList()
    val updatedBoard = engine.boardTiles
    val newScore = engine.score
    val newCombo = engine.comboCount

    if (result.matchedTileIds.isNotEmpty()) {
      // Animate matching items
      _uiState.update { current ->
        current.copy(
          boardTiles = updatedBoard,
          remainingTiles = updatedBoard.size,
          traySlots = updatedTray,
          matchingTileIds = result.matchedTileIds.toSet(),
          isBusy = true
        )
      }

      soundManager.playMatch()

      viewModelScope.launch {
        // Wait 350ms for pop / flash animation
        delay(350L)

        val finalTray = engine.traySlots.toList()
        _uiState.update { current ->
          current.copy(
            traySlots = finalTray,
            matchingTileIds = emptySet(),
            score = newScore,
            comboCount = newCombo,
            isBusy = false
          )
        }

        checkGameOutcome(result.isVictory, result.isGameOver)
      }
    } else {
      // Normal placement
      _uiState.update { current ->
        current.copy(
          boardTiles = updatedBoard,
          remainingTiles = updatedBoard.size,
          traySlots = updatedTray,
          score = newScore,
          comboCount = newCombo
        )
      }

      checkGameOutcome(result.isVictory, result.isGameOver)
    }
  }

  private fun checkGameOutcome(isVictory: Boolean, isGameOver: Boolean) {
    if (isVictory) {
      val state = _uiState.value
      val stars = engine.calculateStars(state.targetScore)
      val coinsEarned = 30 + (stars * 10)
      repository.saveLevelCompletion(state.levelNumber, state.score, stars)
      loadRepositoryData()

      soundManager.playVictory()

      _uiState.update { current ->
        current.copy(
          activeDialog = ActiveDialog.LEVEL_COMPLETE,
          starsEarned = stars,
          coinsEarned = coinsEarned
        )
      }
    } else if (isGameOver) {
      soundManager.playGameOver()
      _uiState.update { current ->
        current.copy(activeDialog = ActiveDialog.GAME_OVER)
      }
    }
  }

  fun onUndo() {
    val state = _uiState.value
    if (state.isBusy) return
    if (state.undoCount <= 0) {
      showDialog(ActiveDialog.SHOP)
      return
    }

    if (engine.undo()) {
      repository.undoCount = state.undoCount - 1
      loadRepositoryData()
      soundManager.playPowerUp()

      _uiState.update { current ->
        current.copy(
          boardTiles = engine.boardTiles,
          remainingTiles = engine.boardTiles.size,
          traySlots = engine.traySlots.toList(),
          hintedTileId = null
        )
      }
    }
  }

  fun onShuffle() {
    val state = _uiState.value
    if (state.isBusy) return
    if (state.shuffleCount <= 0) {
      showDialog(ActiveDialog.SHOP)
      return
    }

    if (engine.shuffleBoard()) {
      repository.shuffleCount = state.shuffleCount - 1
      loadRepositoryData()
      soundManager.playPowerUp()

      _uiState.update { current ->
        current.copy(
          boardTiles = engine.boardTiles,
          hintedTileId = null
        )
      }
    }
  }

  fun onHint() {
    val state = _uiState.value
    if (state.isBusy) return
    if (state.hintCount <= 0) {
      showDialog(ActiveDialog.SHOP)
      return
    }

    val hintId = engine.getHintTileId()
    if (hintId != null) {
      repository.hintCount = state.hintCount - 1
      loadRepositoryData()
      soundManager.playPowerUp()

      _uiState.update { current ->
        current.copy(hintedTileId = hintId)
      }
    }
  }

  fun buyPowerUp(type: String) {
    if (repository.buyPowerUp(type)) {
      soundManager.playPowerUp()
      loadRepositoryData()
      showDialog(ActiveDialog.NONE)
    }
  }

  fun onPause() {
    soundManager.playButtonClick()
    showDialog(ActiveDialog.PAUSE)
  }

  fun onResume() {
    soundManager.playButtonClick()
    showDialog(ActiveDialog.NONE)
  }

  fun onRestartLevel() {
    startLevel(_uiState.value.levelNumber)
  }

  fun onNextLevel() {
    val next = _uiState.value.levelNumber + 1
    if (next <= 100) {
      startLevel(next)
    } else {
      navigateTo(AppScreen.LEVEL_SELECT)
    }
  }

  fun navigateTo(screen: AppScreen) {
    soundManager.playButtonClick()
    _uiState.update { it.copy(currentScreen = screen, activeDialog = ActiveDialog.NONE) }
  }

  fun showDialog(dialog: ActiveDialog) {
    _uiState.update { it.copy(activeDialog = dialog) }
  }

  fun toggleSound() {
    val newSound = !_uiState.value.soundEnabled
    repository.soundEnabled = newSound
    soundManager.soundEnabled = newSound
    _uiState.update { it.copy(soundEnabled = newSound) }
    soundManager.playButtonClick()
  }

  fun toggleMusic() {
    val newMusic = !_uiState.value.musicEnabled
    repository.musicEnabled = newMusic
    _uiState.update { it.copy(musicEnabled = newMusic) }
    soundManager.playButtonClick()
  }

  fun toggleHaptics() {
    val newHaptics = !_uiState.value.hapticsEnabled
    repository.hapticsEnabled = newHaptics
    soundManager.hapticsEnabled = newHaptics
    _uiState.update { it.copy(hapticsEnabled = newHaptics) }
    soundManager.playTap()
  }

  fun resetAllProgress() {
    repository.resetAllProgress()
    loadRepositoryData()
    navigateTo(AppScreen.MENU)
  }
}
