package com.example.engine

import com.example.model.FoodType
import com.example.model.Tile
import kotlin.math.abs
import kotlin.random.Random

data class LevelConfig(
  val levelNumber: Int,
  val totalTiles: Int,
  val distinctFoodTypes: Int,
  val maxLayers: Int,
  val targetScore: Int
)

object LevelGenerator {

  /**
   * Returns a level configuration for any level from 1 to 100+.
   * Tile counts are strictly multiples of 3.
   */
  fun getConfigForLevel(levelNumber: Int): LevelConfig {
    val totalTriples = when {
      levelNumber == 1 -> 5   // 15 tiles
      levelNumber == 2 -> 6   // 18 tiles
      levelNumber == 3 -> 8   // 24 tiles
      levelNumber == 4 -> 10  // 30 tiles
      levelNumber == 5 -> 12  // 36 tiles
      levelNumber <= 10 -> 12 + (levelNumber - 5) // 39, 42, 45, 48, 51
      levelNumber <= 25 -> 14 + (levelNumber / 4) // up to 20 triples (60 tiles)
      levelNumber <= 50 -> 16 + (levelNumber / 5) // up to 26 triples (78 tiles)
      else -> 18 + ((levelNumber.coerceAtMost(100)) / 6) // up to 34 triples
    }

    val totalTiles = totalTriples * 3

    val distinctTypes = when {
      levelNumber == 1 -> 5
      levelNumber == 2 -> 6
      levelNumber <= 4 -> 7
      levelNumber <= 8 -> 8
      levelNumber <= 15 -> 10
      levelNumber <= 30 -> 12
      levelNumber <= 50 -> 14
      else -> 16
    }.coerceAtMost(totalTriples)

    val maxLayers = when {
      levelNumber == 1 -> 1
      levelNumber <= 3 -> 2
      levelNumber <= 15 -> 3
      else -> 4
    }

    val targetScore = totalTriples * 150 + (levelNumber * 100)

    return LevelConfig(
      levelNumber = levelNumber,
      totalTiles = totalTiles,
      distinctFoodTypes = distinctTypes,
      maxLayers = maxLayers,
      targetScore = targetScore
    )
  }

  /**
   * Generates a list of tiles for the level, guaranteed to be mathematically solvable
   * with tiles in multiples of 3 and proper non-overlapping or layer-stacked positions.
   */
  fun generateLevel(levelNumber: Int): List<Tile> {
    val config = getConfigForLevel(levelNumber)
    val random = Random(levelNumber * 1009L + 42)

    // Select distinct food types for this level
    val allFoodTypes = FoodType.values().toList().shuffled(random)
    val chosenFoodTypes = allFoodTypes.take(config.distinctFoodTypes)

    // Distribute triples among the chosen food types
    val totalTriples = config.totalTiles / 3
    val tileFoodList = mutableListOf<FoodType>()

    // Ensure every chosen food type appears at least once
    for (type in chosenFoodTypes) {
      repeat(3) { tileFoodList.add(type) }
    }

    // Fill remaining triples randomly from chosen types
    val remainingTriples = totalTriples - chosenFoodTypes.size
    for (i in 0 until remainingTriples) {
      val type = chosenFoodTypes[random.nextInt(chosenFoodTypes.size)]
      repeat(3) { tileFoodList.add(type) }
    }

    tileFoodList.shuffle(random)

    // Generate coordinates on board
    val positions = generatePositions(config.totalTiles, config.maxLayers, levelNumber, random)

    // Assign IDs and assemble tiles
    var idCounter = 1
    val rawTiles = mutableListOf<Tile>()
    for (i in 0 until config.totalTiles) {
      val pos = positions[i]
      rawTiles.add(
        Tile(
          id = idCounter++,
          type = tileFoodList[i],
          layer = pos.layer,
          gridX = pos.x,
          gridY = pos.y,
          isCovered = false
        )
      )
    }

    // Calculate initial coverage for all tiles
    return updateTileCoverage(rawTiles)
  }

  private data class TilePos(val layer: Int, val x: Float, val y: Float)

  private fun generatePositions(
    count: Int,
    maxLayers: Int,
    levelNumber: Int,
    random: Random
  ): List<TilePos> {
    val positions = mutableListOf<TilePos>()

    // For level 1 (15 tiles, 1 layer): Clean 3 rows of 5 columns or 5x3 grid
    if (maxLayers == 1) {
      val cols = 5
      val rows = 3
      val xOffset = -((cols - 1) * 1.1f) / 2f
      val yOffset = -((rows - 1) * 1.15f) / 2f

      for (r in 0 until rows) {
        for (c in 0 until cols) {
          if (positions.size < count) {
            positions.add(TilePos(layer = 0, x = xOffset + c * 1.1f, y = yOffset + r * 1.15f))
          }
        }
      }
      return positions
    }

    // For multi-layer levels:
    // Create base layers (layer 0, 1, 2, ...) with slight offset to give 3D depth and coverage
    val layerCounts = IntArray(maxLayers)
    if (maxLayers == 2) {
      // e.g. 60% in layer 0, 40% in layer 1
      layerCounts[0] = (count * 0.6f).toInt()
      layerCounts[1] = count - layerCounts[0]
    } else if (maxLayers == 3) {
      // 50% layer 0, 30% layer 1, 20% layer 2
      layerCounts[0] = (count * 0.48f).toInt()
      layerCounts[1] = (count * 0.32f).toInt()
      layerCounts[2] = count - layerCounts[0] - layerCounts[1]
    } else {
      layerCounts[0] = (count * 0.42f).toInt()
      layerCounts[1] = (count * 0.28f).toInt()
      layerCounts[2] = (count * 0.18f).toInt()
      layerCounts[3] = count - layerCounts[0] - layerCounts[1] - layerCounts[2]
    }

    // Place each layer
    for (layer in 0 until maxLayers) {
      val layerCount = layerCounts[layer]
      val cols = when {
        layerCount <= 6 -> 3
        layerCount <= 12 -> 4
        layerCount <= 20 -> 5
        else -> 6
      }
      val rows = (layerCount + cols - 1) / cols

      // Layer offset creates nice staggered stacking
      val staggerX = (layer % 2) * 0.35f
      val staggerY = (layer % 2) * 0.35f
      val xOffset = -((cols - 1) * 1.05f) / 2f + staggerX
      val yOffset = -((rows - 1) * 1.1f) / 2f + staggerY

      var placed = 0
      for (r in 0 until rows) {
        for (c in 0 until cols) {
          if (placed < layerCount && positions.size < count) {
            positions.add(
              TilePos(
                layer = layer,
                x = xOffset + c * 1.05f,
                y = yOffset + r * 1.1f
              )
            )
            placed++
          }
        }
      }
    }

    return positions
  }

  /**
   * Recalculates `isCovered` for each tile.
   * A tile at layer L is covered if any active tile at layer > L overlaps it.
   * Two tiles overlap if horizontal distance < 0.72 and vertical distance < 0.72.
   */
  fun updateTileCoverage(tiles: List<Tile>): List<Tile> {
    val overlapThreshold = 0.72f
    return tiles.map { tile ->
      val covered = tiles.any { other ->
        other.id != tile.id &&
          other.layer > tile.layer &&
          abs(other.gridX - tile.gridX) < overlapThreshold &&
          abs(other.gridY - tile.gridY) < overlapThreshold
      }
      tile.copy(isCovered = covered)
    }
  }
}
