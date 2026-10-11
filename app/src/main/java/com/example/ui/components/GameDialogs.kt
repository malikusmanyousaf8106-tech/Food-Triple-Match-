package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.GameGoldenAmber
import com.example.ui.theme.GameGoldenYellow
import com.example.ui.theme.GameGreenDark
import com.example.ui.theme.GameGreenPlay
import com.example.ui.theme.GameOrangeDark
import com.example.ui.theme.GameOrangePrimary

@Composable
fun VictoryDialog(
  levelNumber: Int,
  score: Int,
  stars: Int,
  coinsEarned: Int,
  onNextLevel: () -> Unit,
  onReplay: () -> Unit,
  onHome: () -> Unit
) {
  Dialog(onDismissRequest = {}) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("dialog_victory"),
      contentAlignment = Alignment.Center
    ) {
      Surface(
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        shadowElevation = 12.dp,
        modifier = Modifier
          .fillMaxWidth()
          .border(2.5.dp, Color(0xFFFFD54F), RoundedCornerShape(28.dp))
      ) {
        Column(
          modifier = Modifier.padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Text(text = "🎉", fontSize = 42.sp)

          Text(
            text = "LEVEL COMPLETE!",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
            color = GameOrangePrimary
          )

          Text(
            text = "Level $levelNumber Cleared",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
          )

          // 3 Stars Row
          Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            for (i in 1..3) {
              val isEarned = i <= stars
              Text(
                text = if (isEarned) "⭐" else "☆",
                fontSize = if (i == 2) 38.sp else 30.sp
              )
            }
          }

          // Score & Coins
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFFFF9E6),
            modifier = Modifier
              .fillMaxWidth()
              .border(1.dp, Color(0xFFFFE082), RoundedCornerShape(16.dp))
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              horizontalArrangement = Arrangement.SpaceAround
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = "SCORE",
                  style = MaterialTheme.typography.labelSmall,
                  color = Color.Gray
                )
                Text(
                  text = "$score",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = GameOrangeDark
                )
              }
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                  text = "REWARD",
                  style = MaterialTheme.typography.labelSmall,
                  color = Color.Gray
                )
                Text(
                  text = "+$coinsEarned 🪙",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = GameGoldenAmber
                )
              }
            }
          }

          // Action Buttons
          Button(
            onClick = onNextLevel,
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("victory_next_level_button"),
            colors = ButtonDefaults.buttonColors(containerColor = GameGreenPlay),
            shape = RoundedCornerShape(16.dp)
          ) {
            Text(
              text = "NEXT LEVEL",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedButton(
              onClick = onReplay,
              modifier = Modifier
                .weight(1f)
                .height(46.dp)
                .testTag("victory_replay_button"),
              shape = RoundedCornerShape(14.dp)
            ) {
              Text("Replay", fontWeight = FontWeight.SemiBold)
            }

            OutlinedButton(
              onClick = onHome,
              modifier = Modifier
                .weight(1f)
                .height(46.dp)
                .testTag("victory_home_button"),
              shape = RoundedCornerShape(14.dp)
            ) {
              Text("Home", fontWeight = FontWeight.SemiBold)
            }
          }
        }
      }
    }
  }
}

@Composable
fun GameOverDialog(
  score: Int,
  onRetry: () -> Unit,
  onHome: () -> Unit
) {
  Dialog(onDismissRequest = {}) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("dialog_game_over"),
      contentAlignment = Alignment.Center
    ) {
      Surface(
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        shadowElevation = 12.dp,
        modifier = Modifier
          .fillMaxWidth()
          .border(2.5.dp, Color(0xFFFF8A80), RoundedCornerShape(28.dp))
      ) {
        Column(
          modifier = Modifier.padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Text(text = "💔", fontSize = 42.sp)

          Text(
            text = "OUT OF SPACE!",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
            color = Color(0xFFD32F2F)
          )

          Text(
            text = "Your tray is full with 6 items and has no triple match available. Don't give up!",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
          )

          Text(
            text = "Final Score: $score",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
          )

          Button(
            onClick = onRetry,
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("game_over_retry_button"),
            colors = ButtonDefaults.buttonColors(containerColor = GameOrangePrimary),
            shape = RoundedCornerShape(16.dp)
          ) {
            Text(
              text = "RETRY LEVEL",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }

          OutlinedButton(
            onClick = onHome,
            modifier = Modifier
              .fillMaxWidth()
              .height(46.dp)
              .testTag("game_over_home_button"),
            shape = RoundedCornerShape(14.dp)
          ) {
            Text("Main Menu", fontWeight = FontWeight.SemiBold)
          }
        }
      }
    }
  }
}

@Composable
fun PauseDialog(
  onResume: () -> Unit,
  onRestart: () -> Unit,
  onHome: () -> Unit,
  soundEnabled: Boolean,
  hapticsEnabled: Boolean,
  onToggleSound: () -> Unit,
  onToggleHaptics: () -> Unit
) {
  Dialog(onDismissRequest = onResume) {
    Surface(
      shape = RoundedCornerShape(28.dp),
      color = Color.White,
      shadowElevation = 10.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("dialog_pause")
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text(
          text = "GAME PAUSED",
          style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
          color = GameOrangePrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Sound Toggle Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Sound Effects", fontWeight = FontWeight.Medium)
          Switch(
            checked = soundEnabled,
            onCheckedChange = { onToggleSound() },
            colors = SwitchDefaults.colors(checkedThumbColor = GameOrangePrimary)
          )
        }

        // Haptics Toggle Row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Vibration", fontWeight = FontWeight.Medium)
          Switch(
            checked = hapticsEnabled,
            onCheckedChange = { onToggleHaptics() },
            colors = SwitchDefaults.colors(checkedThumbColor = GameOrangePrimary)
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Button(
          onClick = onResume,
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("pause_resume_button"),
          colors = ButtonDefaults.buttonColors(containerColor = GameGreenPlay),
          shape = RoundedCornerShape(14.dp)
        ) {
          Text("RESUME", fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
          onClick = onRestart,
          modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .testTag("pause_restart_button"),
          shape = RoundedCornerShape(14.dp)
        ) {
          Text("Restart Level", fontWeight = FontWeight.SemiBold)
        }

        OutlinedButton(
          onClick = onHome,
          modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .testTag("pause_home_button"),
          shape = RoundedCornerShape(14.dp)
        ) {
          Text("Main Menu", fontWeight = FontWeight.SemiBold)
        }
      }
    }
  }
}

@Composable
fun ShopDialog(
  coins: Int,
  onBuy: (String) -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(26.dp),
      color = Color.White,
      shadowElevation = 10.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("dialog_shop")
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "POWER-UP SHOP",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = GameOrangePrimary
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Text(
          text = "Your Balance: $coins 🪙",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
          color = GameGoldenAmber
        )

        // Shop Items
        ShopItemRow("↩️", "Undo Move", 50, coins >= 50) { onBuy("undo") }
        ShopItemRow("🔀", "Shuffle Board", 50, coins >= 50) { onBuy("shuffle") }
        ShopItemRow("💡", "Smart Hint", 50, coins >= 50) { onBuy("hint") }
      }
    }
  }
}

@Composable
private fun ShopItemRow(
  emoji: String,
  title: String,
  cost: Int,
  canAfford: Boolean,
  onBuy: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = Color(0xFFF9F7F5),
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(14.dp))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Text(emoji, fontSize = 24.sp)
        Text(title, fontWeight = FontWeight.SemiBold)
      }

      Button(
        onClick = onBuy,
        enabled = canAfford,
        colors = ButtonDefaults.buttonColors(containerColor = GameGoldenAmber),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text("$cost 🪙", fontWeight = FontWeight.Bold, color = Color.White)
      }
    }
  }
}
