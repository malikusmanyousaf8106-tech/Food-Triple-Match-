package com.example.data

import android.content.Context
import android.content.SharedPreferences

class GameRepository(context: Context) {

  private val prefs: SharedPreferences =
    context.getSharedPreferences("food_triple_match_prefs", Context.MODE_PRIVATE)

  var highestUnlockedLevel: Int
    get() = prefs.getInt(KEY_HIGHEST_UNLOCKED, 1)
    set(value) = prefs.edit().putInt(KEY_HIGHEST_UNLOCKED, value).apply()

  var lastPlayedLevel: Int
    get() = prefs.getInt(KEY_LAST_PLAYED, 1)
    set(value) = prefs.edit().putInt(KEY_LAST_PLAYED, value).apply()

  var totalScore: Int
    get() = prefs.getInt(KEY_TOTAL_SCORE, 0)
    set(value) = prefs.edit().putInt(KEY_TOTAL_SCORE, value).apply()

  var coins: Int
    get() = prefs.getInt(KEY_COINS, 100)
    set(value) = prefs.edit().putInt(KEY_COINS, value).apply()

  var undoCount: Int
    get() = prefs.getInt(KEY_UNDO_COUNT, 3)
    set(value) = prefs.edit().putInt(KEY_UNDO_COUNT, value).apply()

  var shuffleCount: Int
    get() = prefs.getInt(KEY_SHUFFLE_COUNT, 3)
    set(value) = prefs.edit().putInt(KEY_SHUFFLE_COUNT, value).apply()

  var hintCount: Int
    get() = prefs.getInt(KEY_HINT_COUNT, 3)
    set(value) = prefs.edit().putInt(KEY_HINT_COUNT, value).apply()

  var soundEnabled: Boolean
    get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
    set(value) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()

  var musicEnabled: Boolean
    get() = prefs.getBoolean(KEY_MUSIC_ENABLED, true)
    set(value) = prefs.edit().putBoolean(KEY_MUSIC_ENABLED, value).apply()

  var hapticsEnabled: Boolean
    get() = prefs.getBoolean(KEY_HAPTICS_ENABLED, true)
    set(value) = prefs.edit().putBoolean(KEY_HAPTICS_ENABLED, value).apply()

  fun getStarsForLevel(level: Int): Int {
    return prefs.getInt(KEY_LEVEL_STARS_PREFIX + level, 0)
  }

  fun getHighScoreForLevel(level: Int): Int {
    return prefs.getInt(KEY_LEVEL_SCORE_PREFIX + level, 0)
  }

  fun saveLevelCompletion(level: Int, score: Int, stars: Int): Boolean {
    val previousStars = getStarsForLevel(level)
    val previousHigh = getHighScoreForLevel(level)

    val editor = prefs.edit()
    if (stars > previousStars) {
      editor.putInt(KEY_LEVEL_STARS_PREFIX + level, stars)
    }
    if (score > previousHigh) {
      editor.putInt(KEY_LEVEL_SCORE_PREFIX + level, score)
    }

    // Unlock next level up to 100
    val nextLevel = level + 1
    if (nextLevel > highestUnlockedLevel && nextLevel <= 100) {
      editor.putInt(KEY_HIGHEST_UNLOCKED, nextLevel)
    }

    // Reward coins (+30 coins base, +10 per star)
    val coinsEarned = 30 + (stars * 10)
    val newCoins = coins + coinsEarned
    editor.putInt(KEY_COINS, newCoins)

    val newTotal = totalScore + score
    editor.putInt(KEY_TOTAL_SCORE, newTotal)

    editor.apply()
    return true
  }

  fun buyPowerUp(type: String): Boolean {
    val cost = 50
    if (coins < cost) return false
    coins -= cost
    when (type) {
      "undo" -> undoCount++
      "shuffle" -> shuffleCount++
      "hint" -> hintCount++
    }
    return true
  }

  fun resetAllProgress() {
    prefs.edit().clear().apply()
  }

  companion object {
    private const val KEY_HIGHEST_UNLOCKED = "highest_unlocked_level"
    private const val KEY_LAST_PLAYED = "last_played_level"
    private const val KEY_TOTAL_SCORE = "total_score"
    private const val KEY_COINS = "coins"
    private const val KEY_UNDO_COUNT = "undo_count"
    private const val KEY_SHUFFLE_COUNT = "shuffle_count"
    private const val KEY_HINT_COUNT = "hint_count"
    private const val KEY_SOUND_ENABLED = "sound_enabled"
    private const val KEY_MUSIC_ENABLED = "music_enabled"
    private const val KEY_HAPTICS_ENABLED = "haptics_enabled"
    private const val KEY_LEVEL_STARS_PREFIX = "level_stars_"
    private const val KEY_LEVEL_SCORE_PREFIX = "level_score_"
  }
}
