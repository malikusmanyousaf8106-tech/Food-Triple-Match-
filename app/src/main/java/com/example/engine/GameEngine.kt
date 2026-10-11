package com.example.engine

import com.example.model.FoodType
import com.example.model.Tile
import com.example.model.TraySlot
import kotlin.random.Random

data class MoveHistory(
  val tile: Tile,
  val traySlotIndex: Int,
  val previousBoardTiles: List<Tile>
)

data class SelectTileResult(
  val success: Boolean,
  val matchedTypes: List<FoodType> = emptyList(),
  val matchedTileIds: List<Int> = emptyList(),
  val isVictory: Boolean = false,
  val isGameOver: Boolean = false,
  val pointsEarned: Int = 0,
  val comboMultiplier: Float = 1.0f
)

class GameEngine {

  var currentLevel: Int = 1
    private set

  var boardTiles: List<Tile> = emptyList()
    private set

  // Exactly 6 slots: indices 0..5
  var traySlots: MutableList<TraySlot> = MutableList(6) { index -> TraySlot(slotIndex = index) }
    private set

  var score: Int = 0
    private set

  var comboCount: Int = 0
    private set

  private var lastMatchTime: Long = 0L

  private val historyStack = mutableListOf<MoveHistory>()

  fun startLevel(levelNumber: Int) {
    currentLevel = levelNumber
    boardTiles = LevelGenerator.generateLevel(levelNumber)
    traySlots = MutableList(6) { index -> TraySlot(slotIndex = index) }
    historyStack.clear()
    score = 0
    comboCount = 0
    lastMatchTime = 0L
  }

  /**
   * For unit tests or custom scenarios: initializes board directly
   */
  fun setCustomBoard(tiles: List<Tile>) {
    boardTiles = LevelGenerator.updateTileCoverage(tiles)
    traySlots = MutableList(6) { index -> TraySlot(slotIndex = index) }
    historyStack.clear()
    score = 0
    comboCount = 0
  }

  fun canSelectTile(tileId: Int): Boolean {
    val tile = boardTiles.find { it.id == tileId } ?: return false
    if (tile.isCovered) return false
    // Can only select if there is at least one empty slot in tray
    return traySlots.any { it.tile == null }
  }

  fun selectTile(tileId: Int, currentTimeMs: Long = System.currentTimeMillis()): SelectTileResult {
    val tile = boardTiles.find { it.id == tileId }
      ?: return SelectTileResult(success = false)

    if (tile.isCovered) {
      return SelectTileResult(success = false)
    }

    val emptySlotIndex = traySlots.indexOfFirst { it.tile == null }
    if (emptySlotIndex == -1) {
      // Tray is completely full, cannot accept new tile
      return SelectTileResult(success = false, isGameOver = true)
    }

    // Save history before removing from board
    val boardSnapshot = boardTiles.toList()
    historyStack.add(
      MoveHistory(
        tile = tile,
        traySlotIndex = emptySlotIndex,
        previousBoardTiles = boardSnapshot
      )
    )

    // Remove tile from board and update coverage of remaining tiles
    val remainingBoard = boardTiles.filter { it.id != tileId }
    boardTiles = LevelGenerator.updateTileCoverage(remainingBoard)

    // Place into tray slot
    traySlots[emptySlotIndex] = TraySlot(slotIndex = emptySlotIndex, tile = tile)

    // Process triples
    val matchedTypes = mutableListOf<FoodType>()
    val matchedTileIds = mutableListOf<Int>()
    var totalPoints = 0
    var comboMult = 1.0f

    var triplesFound = findTriplesInTray()
    while (triplesFound.isNotEmpty()) {
      for (foodType in triplesFound) {
        val matchingSlots = traySlots.filter { it.tile?.type == foodType }
        val ids = matchingSlots.mapNotNull { it.tile?.id }
        matchedTileIds.addAll(ids)
        matchedTypes.add(foodType)

        // Clear these slots in tray
        for (slot in matchingSlots) {
          traySlots[slot.slotIndex] = TraySlot(slotIndex = slot.slotIndex, tile = null)
        }

        // Calculate score with combo
        if (currentTimeMs - lastMatchTime < 5000L && lastMatchTime > 0) {
          comboCount++
        } else {
          comboCount = 1
        }
        lastMatchTime = currentTimeMs

        comboMult = when (comboCount) {
          1 -> 1.0f
          2 -> 1.5f
          3 -> 2.0f
          else -> 2.5f
        }
        val matchPoints = (150 * comboMult).toInt()
        totalPoints += matchPoints
        score += matchPoints
      }
      triplesFound = findTriplesInTray()
    }

    // Check Victory condition: Board is empty AND tray is empty
    val isVictory = boardTiles.isEmpty() && traySlots.all { it.tile == null }

    // Check Loss condition:
    // Tray has all 6 slots occupied AND no triples can be cleared
    val isGameOver = !isVictory && traySlots.all { it.tile != null } && findTriplesInTray().isEmpty()

    return SelectTileResult(
      success = true,
      matchedTypes = matchedTypes,
      matchedTileIds = matchedTileIds,
      isVictory = isVictory,
      isGameOver = isGameOver,
      pointsEarned = totalPoints,
      comboMultiplier = comboMult
    )
  }

  fun findTriplesInTray(): List<FoodType> {
    val counts = mutableMapOf<FoodType, Int>()
    traySlots.forEach { slot ->
      slot.tile?.let { tile ->
        counts[tile.type] = (counts[tile.type] ?: 0) + 1
      }
    }
    return counts.filter { it.value >= 3 }.keys.toList()
  }

  fun canUndo(): Boolean {
    if (historyStack.isEmpty()) return false
    val lastMove = historyStack.last()
    // Tile must still be in the tray (not cleared as part of a match)
    val slot = traySlots.getOrNull(lastMove.traySlotIndex)
    return slot?.tile?.id == lastMove.tile.id
  }

  fun undo(): Boolean {
    if (!canUndo()) return false
    val lastMove = historyStack.removeAt(historyStack.lastIndex)

    // Remove from tray slot
    traySlots[lastMove.traySlotIndex] = TraySlot(slotIndex = lastMove.traySlotIndex, tile = null)

    // Restore board
    boardTiles = lastMove.previousBoardTiles
    return true
  }

  fun shuffleBoard(seed: Long = System.currentTimeMillis()): Boolean {
    if (boardTiles.size <= 1) return false

    val random = Random(seed)
    // Extract positions
    val positions = boardTiles.map { Triple(it.layer, it.gridX, it.gridY) }.shuffled(random)

    // Reassign positions while preserving tiles
    val newTiles = boardTiles.mapIndexed { index, tile ->
      val pos = positions[index]
      tile.copy(
        layer = pos.first,
        gridX = pos.second,
        gridY = pos.third
      )
    }

    boardTiles = LevelGenerator.updateTileCoverage(newTiles)
    return true
  }

  fun getHintTileId(): Int? {
    if (boardTiles.isEmpty()) return null

    val accessibleBoardTiles = boardTiles.filter { !it.isCovered }
    if (accessibleBoardTiles.isEmpty()) return null

    // Tray analysis
    val trayTypes = traySlots.mapNotNull { it.tile?.type }
    val trayTypeCounts = trayTypes.groupingBy { it }.eachCount()

    // 1. If tray has 2 identical items, prioritize finding the 3rd on the board
    for ((type, count) in trayTypeCounts) {
      if (count == 2) {
        val candidate = accessibleBoardTiles.find { it.type == type }
        if (candidate != null) return candidate.id
      }
    }

    // 2. If tray has 1 item, and there are 2 accessible on board
    for ((type, count) in trayTypeCounts) {
      if (count == 1) {
        val candidates = accessibleBoardTiles.filter { it.type == type }
        if (candidates.size >= 2) return candidates.first().id
      }
    }

    // 3. Look for 3 identical accessible tiles on the board
    val accessibleTypeCounts = accessibleBoardTiles.groupingBy { it.type }.eachCount()
    for ((type, count) in accessibleTypeCounts) {
      if (count >= 3) {
        val candidate = accessibleBoardTiles.find { it.type == type }
        if (candidate != null) return candidate.id
      }
    }

    // 4. Fallback: Any accessible tile that matches any tray tile, or just first accessible tile
    for (type in trayTypes) {
      val candidate = accessibleBoardTiles.find { it.type == type }
      if (candidate != null) return candidate.id
    }

    return accessibleBoardTiles.firstOrNull()?.id
  }

  fun calculateStars(levelTargetScore: Int): Int {
    return when {
      score >= (levelTargetScore * 1.25f).toInt() -> 3
      score >= (levelTargetScore * 0.9f).toInt() -> 2
      else -> 1
    }
  }
}
