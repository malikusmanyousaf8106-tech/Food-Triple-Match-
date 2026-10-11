package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TraySlot
import com.example.ui.theme.GameGoldenYellow
import com.example.ui.theme.GameSlotBg
import com.example.ui.theme.GameSlotBorder
import com.example.ui.theme.GameWoodTrayBg
import com.example.ui.theme.GameWoodTrayBorder

@Composable
fun CollectionTrayView(
  slots: List<TraySlot>,
  modifier: Modifier = Modifier,
  matchingTileIds: Set<Int> = emptySet(),
  slotSize: Dp = 68.dp
) {
  // Guard: exactly 6 slots
  val displaySlots = (0 until 6).map { idx ->
    slots.find { it.slotIndex == idx } ?: TraySlot(slotIndex = idx)
  }

  val containerShape = RoundedCornerShape(22.dp)

  Column(
    modifier = modifier
      .fillMaxWidth()
      .widthIn(max = 420.dp)
      .padding(horizontal = 16.dp, vertical = 6.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Header label
    Row(
      modifier = Modifier.padding(bottom = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Text(
        text = "COLLECTION TRAY",
        style = MaterialTheme.typography.labelMedium.copy(
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.2.sp
        ),
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
      )
      val occupiedCount = displaySlots.count { it.tile != null }
      Text(
        text = "($occupiedCount/6)",
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Bold
        ),
        color = if (occupiedCount >= 5) Color(0xFFD32F2F) else MaterialTheme.colorScheme.primary
      )
    }

    // Outer Tray Box with wooden/soft-candy depth
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .shadow(6.dp, containerShape)
        .clip(containerShape)
        .background(
          Brush.verticalGradient(
            colors = listOf(
              GameWoodTrayBg,
              Color(0xFFEADBCE)
            )
          )
        )
        .border(2.5.dp, GameWoodTrayBorder, containerShape)
        .padding(10.dp),
      contentAlignment = Alignment.Center
    ) {
      // 2 Rows of 3 Slots
      Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Row 1: slots 0, 1, 2
        Row(
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          for (i in 0..2) {
            val slot = displaySlots[i]
            val isSlotMatching = slot.tile != null && matchingTileIds.contains(slot.tile.id)
            TraySlotItem(
              slot = slot,
              isMatching = isSlotMatching,
              slotSize = slotSize
            )
          }
        }

        // Row 2: slots 3, 4, 5
        Row(
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          for (i in 3..5) {
            val slot = displaySlots[i]
            val isSlotMatching = slot.tile != null && matchingTileIds.contains(slot.tile.id)
            TraySlotItem(
              slot = slot,
              isMatching = isSlotMatching,
              slotSize = slotSize
            )
          }
        }
      }
    }
  }
}

@Composable
fun TraySlotItem(
  slot: TraySlot,
  isMatching: Boolean,
  slotSize: Dp = 68.dp,
  modifier: Modifier = Modifier
) {
  val slotShape = RoundedCornerShape(14.dp)
  val tile = slot.tile

  // Pulse animation when 3 items match together
  val infiniteTransition = rememberInfiniteTransition(label = "match_pulse")
  val matchScale by infiniteTransition.animateFloat(
    initialValue = 1.0f,
    targetValue = 1.18f,
    animationSpec = infiniteRepeatable(
      animation = tween(180, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "slot_match_scale"
  )

  Box(
    modifier = modifier
      .size(slotSize)
      .testTag("tray_slot_${slot.slotIndex}"),
    contentAlignment = Alignment.Center
  ) {
    // Recessed empty slot base
    Box(
      modifier = Modifier
        .fillMaxSize()
        .clip(slotShape)
        .background(GameSlotBg)
        .border(
          width = 1.5.dp,
          color = GameSlotBorder,
          shape = slotShape
        ),
      contentAlignment = Alignment.Center
    ) {
      if (tile == null) {
        // Faint placeholder number / outline
        Text(
          text = "${slot.slotIndex + 1}",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFFD7CCC8).copy(alpha = 0.6f)
        )
      }
    }

    // Occupied Slot Item
    if (tile != null) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .scale(if (isMatching) matchScale else 1.0f)
      ) {
        Surface(
          shape = slotShape,
          color = Color.White,
          shadowElevation = if (isMatching) 8.dp else 4.dp,
          modifier = Modifier
            .fillMaxSize()
            .clip(slotShape)
            .border(
              width = if (isMatching) 3.dp else 1.5.dp,
              color = if (isMatching) GameGoldenYellow else tile.type.borderColor.copy(alpha = 0.45f),
              shape = slotShape
            )
        ) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.verticalGradient(
                  colors = if (isMatching) {
                    listOf(Color(0xFFFFF9C4), Color(0xFFFFE082))
                  } else {
                    listOf(tile.type.secondaryColor, Color.White)
                  }
                )
              ),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = tile.type.emoji,
              fontSize = (slotSize.value * 0.48f).sp,
              modifier = Modifier.testTag("tray_tile_${tile.id}")
            )

            if (isMatching) {
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
  }
}
