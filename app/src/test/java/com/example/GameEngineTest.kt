package com.example

import com.example.engine.GameEngine
import com.example.engine.LevelGenerator
import com.example.model.FoodType
import com.example.model.Tile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GameEngineTest {

  private lateinit var engine: GameEngine

  @Before
  fun setUp() {
    engine = GameEngine()
  }

  @Test
  fun testCollectingOneTile() {
    val tiles = listOf(
      Tile(id = 1, type = FoodType.APPLE, layer = 0, gridX = 0f, gridY = 0f),
      Tile(id = 2, type = FoodType.BANANA, layer = 0, gridX = 1f, gridY = 0f)
    )
    engine.setCustomBoard(tiles)

    val result = engine.selectTile(1)
    assertTrue(result.success)
    assertEquals(1, engine.traySlots.count { it.tile != null })
    assertEquals(FoodType.APPLE, engine.traySlots[0].tile?.type)
    assertEquals(1, engine.boardTiles.size)
  }

  @Test
  fun testCollectingTwoIdenticalTiles() {
    val tiles = listOf(
      Tile(id = 1, type = FoodType.APPLE, layer = 0, gridX = 0f, gridY = 0f),
      Tile(id = 2, type = FoodType.APPLE, layer = 0, gridX = 1f, gridY = 0f)
    )
    engine.setCustomBoard(tiles)

    engine.selectTile(1)
    val result = engine.selectTile(2)

    assertTrue(result.success)
    assertEquals(2, engine.traySlots.count { it.tile != null })
    assertTrue(result.matchedTileIds.isEmpty())
    assertFalse(result.isVictory)
  }

  @Test
  fun testCollectingThirdIdenticalTileRemovesTriple() {
    val tiles = listOf(
      Tile(id = 1, type = FoodType.APPLE, layer = 0, gridX = 0f, gridY = 0f),
      Tile(id = 2, type = FoodType.APPLE, layer = 0, gridX = 1f, gridY = 0f),
      Tile(id = 3, type = FoodType.APPLE, layer = 0, gridX = 2f, gridY = 0f),
      Tile(id = 4, type = FoodType.BURGER, layer = 0, gridX = 0f, gridY = 1f)
    )
    engine.setCustomBoard(tiles)

    engine.selectTile(1)
    engine.selectTile(2)
    val result = engine.selectTile(3)

    assertTrue(result.success)
    assertEquals(3, result.matchedTileIds.size)
    assertEquals(listOf(FoodType.APPLE), result.matchedTypes)
    // 3 Apples cleared from tray, only 0 items in tray
    assertEquals(0, engine.traySlots.count { it.tile != null })
    assertTrue(engine.score > 0)
    assertFalse(result.isVictory) // Burger is still on board
  }

  @Test
  fun testUnmatchedTilesRemainInTrayWhenTripleClears() {
    val tiles = listOf(
      Tile(id = 1, type = FoodType.APPLE, layer = 0, gridX = 0f, gridY = 0f),
      Tile(id = 2, type = FoodType.BURGER, layer = 0, gridX = 1f, gridY = 0f),
      Tile(id = 3, type = FoodType.APPLE, layer = 0, gridX = 2f, gridY = 0f),
      Tile(id = 4, type = FoodType.APPLE, layer = 0, gridX = 0f, gridY = 1f)
    )
    engine.setCustomBoard(tiles)

    engine.selectTile(1) // Apple
    engine.selectTile(2) // Burger
    engine.selectTile(3) // Apple
    val result = engine.selectTile(4) // Apple

    assertTrue(result.success)
    assertEquals(3, result.matchedTileIds.size)
    // Burger remains in tray!
    assertEquals(1, engine.traySlots.count { it.tile != null })
    val remainingTile = engine.traySlots.find { it.tile != null }?.tile
    assertEquals(FoodType.BURGER, remainingTile?.type)
  }

  @Test
  fun testVictoryConditionAllBoardAndTrayEmpty() {
    val tiles = listOf(
      Tile(id = 1, type = FoodType.PIZZA, layer = 0, gridX = 0f, gridY = 0f),
      Tile(id = 2, type = FoodType.PIZZA, layer = 0, gridX = 1f, gridY = 0f),
      Tile(id = 3, type = FoodType.PIZZA, layer = 0, gridX = 2f, gridY = 0f)
    )
    engine.setCustomBoard(tiles)

    engine.selectTile(1)
    engine.selectTile(2)
    val result = engine.selectTile(3)

    assertTrue(result.success)
    assertTrue(result.isVictory)
    assertFalse(result.isGameOver)
    assertEquals(0, engine.boardTiles.size)
    assertEquals(0, engine.traySlots.count { it.tile != null })
  }

  @Test
  fun testGameOverWhenTrayFullWithNoMatches() {
    // 6 distinct foods
    val tiles = listOf(
      Tile(id = 1, type = FoodType.APPLE, layer = 0, gridX = 0f, gridY = 0f),
      Tile(id = 2, type = FoodType.BANANA, layer = 0, gridX = 1f, gridY = 0f),
      Tile(id = 3, type = FoodType.ORANGE, layer = 0, gridX = 2f, gridY = 0f),
      Tile(id = 4, type = FoodType.GRAPES, layer = 0, gridX = 0f, gridY = 1f),
      Tile(id = 5, type = FoodType.BURGER, layer = 0, gridX = 1f, gridY = 1f),
      Tile(id = 6, type = FoodType.PIZZA, layer = 0, gridX = 2f, gridY = 1f),
      Tile(id = 7, type = FoodType.DONUT, layer = 0, gridX = 0f, gridY = 2f)
    )
    engine.setCustomBoard(tiles)

    for (i in 1..5) {
      val res = engine.selectTile(i)
      assertFalse(res.isGameOver)
    }

    // Selecting 6th tile fills the tray without any triple
    val res6 = engine.selectTile(6)
    assertTrue(res6.success)
    assertTrue(res6.isGameOver)
    assertFalse(res6.isVictory)
  }

  @Test
  fun testSixthSlotClearingTriplePreventsGameOver() {
    // 2 Apples, 3 different items, then 3rd Apple as 6th selection
    val tiles = listOf(
      Tile(id = 1, type = FoodType.APPLE, layer = 0, gridX = 0f, gridY = 0f),
      Tile(id = 2, type = FoodType.BANANA, layer = 0, gridX = 1f, gridY = 0f),
      Tile(id = 3, type = FoodType.APPLE, layer = 0, gridX = 2f, gridY = 0f),
      Tile(id = 4, type = FoodType.ORANGE, layer = 0, gridX = 0f, gridY = 1f),
      Tile(id = 5, type = FoodType.BURGER, layer = 0, gridX = 1f, gridY = 1f),
      Tile(id = 6, type = FoodType.APPLE, layer = 0, gridX = 2f, gridY = 1f), // 3rd Apple!
      Tile(id = 7, type = FoodType.DONUT, layer = 0, gridX = 0f, gridY = 2f)
    )
    engine.setCustomBoard(tiles)

    for (i in 1..5) {
      engine.selectTile(i)
    }
    assertEquals(5, engine.traySlots.count { it.tile != null })

    // 6th tile completes 3 Apples
    val result = engine.selectTile(6)
    assertTrue(result.success)
    // 3 Apples matched and cleared!
    assertEquals(listOf(FoodType.APPLE), result.matchedTypes)
    assertFalse(result.isGameOver)
    // Tray now has only 3 items (Banana, Orange, Burger), freeing 3 slots
    assertEquals(3, engine.traySlots.count { it.tile != null })
  }

  @Test
  fun testUndoRestoresTileToBoard() {
    val tiles = listOf(
      Tile(id = 1, type = FoodType.AVOCADO, layer = 0, gridX = 0f, gridY = 0f),
      Tile(id = 2, type = FoodType.CARROT, layer = 0, gridX = 1f, gridY = 0f)
    )
    engine.setCustomBoard(tiles)

    assertTrue(engine.selectTile(1).success)
    assertEquals(1, engine.boardTiles.size)
    assertEquals(1, engine.traySlots.count { it.tile != null })
    assertTrue(engine.canUndo())

    val undone = engine.undo()
    assertTrue(undone)
    assertEquals(2, engine.boardTiles.size)
    assertEquals(0, engine.traySlots.count { it.tile != null })
    assertTrue(engine.boardTiles.any { it.id == 1 })
  }

  @Test
  fun testShuffleBoardPreservesCountAndTypes() {
    engine.startLevel(1)
    val originalCount = engine.boardTiles.size
    val originalTypeCounts = engine.boardTiles.groupingBy { it.type }.eachCount()

    val shuffled = engine.shuffleBoard(seed = 12345L)
    assertTrue(shuffled)
    assertEquals(originalCount, engine.boardTiles.size)
    val newTypeCounts = engine.boardTiles.groupingBy { it.type }.eachCount()
    assertEquals(originalTypeCounts, newTypeCounts)
  }

  @Test
  fun testHintReturnsAvailableMove() {
    val tiles = listOf(
      Tile(id = 1, type = FoodType.DONUT, layer = 0, gridX = 0f, gridY = 0f),
      Tile(id = 2, type = FoodType.DONUT, layer = 0, gridX = 1f, gridY = 0f),
      Tile(id = 3, type = FoodType.DONUT, layer = 0, gridX = 2f, gridY = 0f)
    )
    engine.setCustomBoard(tiles)

    val hintId = engine.getHintTileId()
    assertNotNull(hintId)
    assertTrue(tiles.any { it.id == hintId })
  }

  @Test
  fun testCoveredTileCannotBeSelected() {
    // Tile 2 is directly over Tile 1 on layer 1
    val tile1 = Tile(id = 1, type = FoodType.APPLE, layer = 0, gridX = 0f, gridY = 0f)
    val tile2 = Tile(id = 2, type = FoodType.BANANA, layer = 1, gridX = 0f, gridY = 0f)
    engine.setCustomBoard(listOf(tile1, tile2))

    val coveredTile = engine.boardTiles.find { it.id == 1 }!!
    assertTrue(coveredTile.isCovered)

    // Attempt to select covered tile should fail
    val result = engine.selectTile(1)
    assertFalse(result.success)

    // Select top tile 2
    assertTrue(engine.selectTile(2).success)

    // Now tile 1 is uncovered!
    val nowUncovered = engine.boardTiles.find { it.id == 1 }!!
    assertFalse(nowUncovered.isCovered)

    // Now tile 1 can be selected
    assertTrue(engine.selectTile(1).success)
  }

  @Test
  fun testLevelConfigurationsMultiplesOfThree() {
    for (lvl in 1..100) {
      val config = LevelGenerator.getConfigForLevel(lvl)
      assertEquals(0, config.totalTiles % 3)
      val tiles = LevelGenerator.generateLevel(lvl)
      assertEquals(config.totalTiles, tiles.size)

      // Verify every food type in level is multiple of 3
      val counts = tiles.groupingBy { it.type }.eachCount()
      for ((type, count) in counts) {
        assertEquals("Level $lvl food $type must be multiple of 3", 0, count % 3)
      }
    }
  }
}
