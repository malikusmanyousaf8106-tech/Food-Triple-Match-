package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Tile

@Composable
fun FoodTileView(
  tile: Tile,
  modifier: Modifier = Modifier,
  tileSize: Dp = 60.dp,
  isHinted: Boolean = false,
  isMatching: Boolean = false,
  onClick: () -> Unit = {}
) {
  val isCovered = tile.isCovered
  val foodType = tile.type

  // Hint pulsing animation
  val infiniteTransition = rememberInfiniteTransition(label = "hint_pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1.0f,
    targetValue = 1.14f,
    animationSpec = infiniteRepeatable(
      animation = tween(450, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_scale"
  )

  val currentScale = when {
    isMatching -> 1.15f
    isHinted -> pulseScale
    else -> 1.0f
  }

  val shape = RoundedCornerShape(14.dp)

  Box(
    modifier = modifier
      .size(tileSize)
      .scale(currentScale)
      .testTag("food_tile_${tile.id}"),
    contentAlignment = Alignment.Center
  ) {
    // 3D Shadow layer
    Box(
      modifier = Modifier
        .fillMaxSize()
        .offset(y = if (isCovered) 1.dp else 4.dp)
        .clip(shape)
        .background(
          if (isCovered) Color(0x33000000) else Color(0x2E000000)
        )
    )

    // Main Card Surface
    Surface(
      shape = shape,
      color = if (isCovered) Color(0xFFE8E5E0) else Color.White,
      shadowElevation = if (isCovered) 1.dp else 4.dp,
      modifier = Modifier
        .fillMaxSize()
        .clip(shape)
        .border(
          width = if (isHinted) 2.5.dp else if (isCovered) 1.dp else 1.5.dp,
          color = when {
            isHinted -> Color(0xFFFFB300)
            isMatching -> Color(0xFFFFD54F)
            isCovered -> Color(0xFFBDBDBD)
            else -> foodType.borderColor.copy(alpha = 0.35f)
          },
          shape = shape
        )
        .clickable(
          enabled = !isCovered,
          role = Role.Button,
          interactionSource = remember { MutableInteractionSource() },
          indication = null,
          onClick = onClick
        )
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              colors = if (isCovered) {
                listOf(Color(0xFFE0DDD9), Color(0xFFCEC9C4))
              } else {
                listOf(
                  foodType.secondaryColor.copy(alpha = 0.7f),
                  Color.White
                )
              }
            )
          ),
        contentAlignment = Alignment.Center
      ) {
        // Food Emoji
        Text(
          text = foodType.emoji,
          fontSize = (tileSize.value * 0.44f).sp,
          modifier = Modifier
            .alpha(if (isCovered) 0.50f else 1.0f)
            .testTag("food_tile_emoji_${tile.id}")
        )

        // Lock icon if tile is covered
        if (isCovered) {
          Box(
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .padding(4.dp)
              .size(16.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0x99424242)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = "Covered tile",
              tint = Color.White,
              modifier = Modifier.size(10.dp)
            )
          }
        }

        // Hint sparkle badge
        if (isHinted) {
          Text(
            text = "✨",
            fontSize = 12.sp,
            modifier = Modifier
              .align(Alignment.TopEnd)
              .padding(2.dp)
          )
        }
      }
    }
  }
}
