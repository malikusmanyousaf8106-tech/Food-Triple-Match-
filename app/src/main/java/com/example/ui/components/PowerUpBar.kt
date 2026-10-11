package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GameBlueSky
import com.example.ui.theme.GameGoldenAmber
import com.example.ui.theme.GameGoldenYellow
import com.example.ui.theme.GamePurpleFun

@Composable
fun PowerUpBar(
  undoCount: Int,
  shuffleCount: Int,
  hintCount: Int,
  coins: Int,
  onUndoClick: () -> Unit,
  onShuffleClick: () -> Unit,
  onHintClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 24.dp, vertical = 6.dp),
    horizontalArrangement = Arrangement.SpaceEvenly,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Undo Button
    PowerUpButton(
      iconEmoji = "↩️",
      title = "Undo",
      count = undoCount,
      accentColor = GameBlueSky,
      testTag = "powerup_undo",
      onClick = onUndoClick
    )

    // Shuffle Button
    PowerUpButton(
      iconEmoji = "🔀",
      title = "Shuffle",
      count = shuffleCount,
      accentColor = GamePurpleFun,
      testTag = "powerup_shuffle",
      onClick = onShuffleClick
    )

    // Hint Button
    PowerUpButton(
      iconEmoji = "💡",
      title = "Hint",
      count = hintCount,
      accentColor = GameGoldenAmber,
      testTag = "powerup_hint",
      onClick = onHintClick
    )
  }
}

@Composable
fun PowerUpButton(
  iconEmoji: String,
  title: String,
  count: Int,
  accentColor: Color,
  testTag: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val shape = RoundedCornerShape(16.dp)

  Column(
    modifier = modifier.clickable(onClick = onClick),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(
      modifier = Modifier
        .size(56.dp)
        .shadow(4.dp, shape)
        .clip(shape)
        .background(
          Brush.verticalGradient(
            colors = listOf(
              Color.White,
              accentColor.copy(alpha = 0.15f)
            )
          )
        )
        .border(1.5.dp, accentColor.copy(alpha = 0.6f), shape)
        .testTag(testTag),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = iconEmoji,
        fontSize = 24.sp
      )

      // Count badge
      Box(
        modifier = Modifier
          .align(Alignment.TopEnd)
          .offset(x = 2.dp, y = (-2).dp)
          .size(20.dp)
          .clip(CircleShape)
          .background(if (count > 0) accentColor else Color(0xFFE53935))
          .border(1.5.dp, Color.White, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = if (count > 0) "$count" else "+",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }
    }

    Text(
      text = title,
      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
      color = MaterialTheme.colorScheme.onBackground,
      modifier = Modifier.padding(top = 4.dp)
    )
  }
}
