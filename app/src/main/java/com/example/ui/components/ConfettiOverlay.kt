package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

private data class Particle(
  val startX: Float,
  val speedY: Float,
  val speedX: Float,
  val color: Color,
  val size: Float,
  val rotationSpeed: Float
)

@Composable
fun ConfettiOverlay(
  modifier: Modifier = Modifier
) {
  val animProgress = remember { Animatable(0f) }

  val particles = remember {
    val colors = listOf(
      Color(0xFFFF5252),
      Color(0xFFFFD700),
      Color(0xFF4CAF50),
      Color(0xFF29B6F6),
      Color(0xFFAB47BC),
      Color(0xFFFF7043)
    )
    val random = Random(42)
    List(75) {
      Particle(
        startX = random.nextFloat(),
        speedY = 0.5f + random.nextFloat() * 0.8f,
        speedX = (random.nextFloat() - 0.5f) * 0.3f,
        color = colors[random.nextInt(colors.size)],
        size = 12f + random.nextFloat() * 14f,
        rotationSpeed = (random.nextFloat() - 0.5f) * 720f
      )
    }
  }

  LaunchedEffect(Unit) {
    animProgress.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 2400, easing = LinearEasing)
    )
  }

  Canvas(modifier = modifier.fillMaxSize()) {
    val progress = animProgress.value
    particles.forEach { p ->
      val currentX = (p.startX + p.speedX * progress) * size.width
      val currentY = (progress * p.speedY) * size.height * 1.2f - 40f
      val alpha = (1f - progress * 0.7f).coerceIn(0f, 1f)

      drawRect(
        color = p.color.copy(alpha = alpha),
        topLeft = Offset(currentX, currentY),
        size = Size(p.size, p.size * 0.65f)
      )
    }
  }
}
