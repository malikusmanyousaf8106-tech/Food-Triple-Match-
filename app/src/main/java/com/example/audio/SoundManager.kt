package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class SoundManager(private val context: Context) {

  private val scope = CoroutineScope(Dispatchers.Default)

  var soundEnabled: Boolean = true
  var hapticsEnabled: Boolean = true

  private val vibrator: Vibrator? by lazy {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
      vibratorManager?.defaultVibrator
    } else {
      @Suppress("DEPRECATION")
      context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
  }

  fun playTap() {
    vibrate(20)
    if (!soundEnabled) return
    scope.launch {
      playTone(frequency = 587.33, durationMs = 45, volume = 0.5f) // D5
    }
  }

  fun playMatch() {
    vibrate(60)
    if (!soundEnabled) return
    scope.launch {
      // Happy two-tone sparkle
      playTone(frequency = 523.25, durationMs = 60, volume = 0.6f) // C5
      playTone(frequency = 659.25, durationMs = 70, volume = 0.7f) // E5
      playTone(frequency = 783.99, durationMs = 110, volume = 0.8f) // G5
    }
  }

  fun playVictory() {
    vibratePattern(longArrayOf(0, 50, 40, 70, 40, 120))
    if (!soundEnabled) return
    scope.launch {
      playTone(frequency = 523.25, durationMs = 90, volume = 0.7f) // C5
      playTone(frequency = 659.25, durationMs = 90, volume = 0.7f) // E5
      playTone(frequency = 783.99, durationMs = 90, volume = 0.8f) // G5
      playTone(frequency = 1046.50, durationMs = 240, volume = 0.9f) // C6
    }
  }

  fun playGameOver() {
    vibrate(100)
    if (!soundEnabled) return
    scope.launch {
      playTone(frequency = 392.00, durationMs = 120, volume = 0.6f) // G4
      playTone(frequency = 349.23, durationMs = 130, volume = 0.6f) // F4
      playTone(frequency = 293.66, durationMs = 220, volume = 0.6f) // D4
    }
  }

  fun playPowerUp() {
    vibrate(35)
    if (!soundEnabled) return
    scope.launch {
      playTone(frequency = 440.0, durationMs = 50, volume = 0.6f)
      playTone(frequency = 880.0, durationMs = 90, volume = 0.7f)
    }
  }

  fun playButtonClick() {
    vibrate(15)
    if (!soundEnabled) return
    scope.launch {
      playTone(frequency = 440.0, durationMs = 35, volume = 0.4f)
    }
  }

  private fun vibrate(durationMs: Long) {
    if (!hapticsEnabled) return
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(durationMs)
      }
    } catch (_: Exception) {}
  }

  private fun vibratePattern(timings: LongArray) {
    if (!hapticsEnabled) return
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator?.vibrate(VibrationEffect.createWaveform(timings, -1))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(timings, -1)
      }
    } catch (_: Exception) {}
  }

  private fun playTone(frequency: Double, durationMs: Int, volume: Float) {
    try {
      val sampleRate = 22050
      val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
      val buffer = ShortArray(numSamples)

      for (i in 0 until numSamples) {
        val time = i.toDouble() / sampleRate
        // Envelope: smooth attack and decay
        val envelope = when {
          i < numSamples * 0.1 -> (i / (numSamples * 0.1))
          i > numSamples * 0.7 -> ((numSamples - i) / (numSamples * 0.3))
          else -> 1.0
        }
        val angle = 2.0 * PI * frequency * time
        val sample = (sin(angle) * envelope * volume * Short.MAX_VALUE).toInt()
        buffer[i] = sample.toShort()
      }

      val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

      val audioFormat = AudioFormat.Builder()
        .setSampleRate(sampleRate)
        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
        .build()

      val track = AudioTrack.Builder()
        .setAudioAttributes(audioAttributes)
        .setAudioFormat(audioFormat)
        .setBufferSizeInBytes(buffer.size * 2)
        .setTransferMode(AudioTrack.MODE_STATIC)
        .build()

      track.write(buffer, 0, buffer.size)
      track.play()
      // Let track play and then release
      Thread.sleep((durationMs + 20).toLong())
      track.release()
    } catch (_: Exception) {}
  }
}
