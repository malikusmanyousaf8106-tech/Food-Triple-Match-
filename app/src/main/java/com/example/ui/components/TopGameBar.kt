package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.ui.theme.GameGoldenAmber
import com.example.ui.theme.GameGoldenYellow
import com.example.ui.theme.GameGreenPlay
import com.example.ui.theme.GameOrangePrimary

@Composable
fun TopGameBar(
  levelNumber: Int,
  score: Int,
  targetScore: Int,
  remainingTiles: Int,
  totalTiles: Int,
  comboCount: Int,
  soundEnabled: Boolean,
  onHomeClick: () -> Unit,
  onPauseClick: () -> Unit,
  onSoundToggle: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 12.dp, vertical = 4.dp),
    verticalArrangement = Arrangement.spacedBy(6.dp)
  ) {
    // Top Row: Home | Level Badge | Sound & Pause
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Home Button
      GameCircleButton(
        icon = Icons.Default.Home,
        contentDesc = "Home",
        testTag = "top_bar_home",
        onClick = onHomeClick
      )

      // Level Badge
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = GameOrangePrimary,
        shadowElevation = 3.dp,
        modifier = Modifier.border(1.5.dp, Color(0xFFFFB74D), RoundedCornerShape(20.dp))
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text("👑", fontSize = 14.sp)
          Text(
            text = "Level $levelNumber",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
          )
        }
      }

      // Actions: Sound & Pause
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        GameCircleButton(
          icon = if (soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
          contentDesc = "Sound Toggle",
          testTag = "top_bar_sound",
          onClick = onSoundToggle
        )
        GameCircleButton(
          icon = Icons.Default.Pause,
          contentDesc = "Pause",
          testTag = "top_bar_pause",
          onClick = onPauseClick
        )
      }
    }

    // Progress Area: Score | Progress Bar | Remaining Tiles
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      // Score badge
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier.border(1.dp, Color(0xFFFFE082), RoundedCornerShape(12.dp))
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Text("⭐", fontSize = 12.sp)
          Text(
            text = "$score",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = GameOrangePrimary
          )
          if (comboCount > 1) {
            Text(
              text = "x$comboCount",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 12.sp,
              color = GameGreenPlay
            )
          }
        }
      }

      // Progress bar towards clearing all tiles
      val progress = if (totalTiles > 0) {
        ((totalTiles - remainingTiles).toFloat() / totalTiles).coerceIn(0f, 1f)
      } else 0f

      Box(
        modifier = Modifier
          .weight(1f)
          .padding(horizontal = 10.dp)
      ) {
        LinearProgressIndicator(
          progress = { progress },
          modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(5.dp)),
          color = GameGreenPlay,
          trackColor = Color(0xFFE0E0E0)
        )
      }

      // Remaining tiles badge
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier.border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Text("🍎", fontSize = 12.sp)
          Text(
            text = "$remainingTiles left",
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground
          )
        }
      }
    }
  }
}

@Composable
fun GameCircleButton(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  contentDesc: String,
  testTag: String,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .size(42.dp)
      .shadow(3.dp, CircleShape)
      .clip(CircleShape)
      .background(Color.White)
      .border(1.5.dp, Color(0xFFFFCC80), CircleShape)
      .clickable(onClick = onClick)
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = icon,
      contentDescription = contentDesc,
      tint = GameOrangePrimary,
      modifier = Modifier.size(20.dp)
    )
  }
}
