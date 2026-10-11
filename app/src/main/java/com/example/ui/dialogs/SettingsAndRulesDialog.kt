package com.example.ui.dialogs

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.GameOrangePrimary

@Composable
fun SettingsDialog(
  soundEnabled: Boolean,
  musicEnabled: Boolean,
  hapticsEnabled: Boolean,
  onToggleSound: () -> Unit,
  onToggleMusic: () -> Unit,
  onToggleHaptics: () -> Unit,
  onResetProgress: () -> Unit,
  onDismiss: () -> Unit
) {
  var showResetConfirm by remember { mutableStateOf(false) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(26.dp),
      color = Color.White,
      shadowElevation = 10.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("dialog_settings")
    ) {
      Column(
        modifier = Modifier.padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "SETTINGS",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = GameOrangePrimary
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        // Sound Toggle
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Sound Effects 🔊", fontWeight = FontWeight.Medium)
          Switch(
            checked = soundEnabled,
            onCheckedChange = { onToggleSound() },
            colors = SwitchDefaults.colors(checkedThumbColor = GameOrangePrimary)
          )
        }

        // Music Toggle
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Background Music 🎵", fontWeight = FontWeight.Medium)
          Switch(
            checked = musicEnabled,
            onCheckedChange = { onToggleMusic() },
            colors = SwitchDefaults.colors(checkedThumbColor = GameOrangePrimary)
          )
        }

        // Haptics Toggle
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Vibration / Haptics 📳", fontWeight = FontWeight.Medium)
          Switch(
            checked = hapticsEnabled,
            onCheckedChange = { onToggleHaptics() },
            colors = SwitchDefaults.colors(checkedThumbColor = GameOrangePrimary)
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (!showResetConfirm) {
          OutlinedButton(
            onClick = { showResetConfirm = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Reset Game Progress", color = Color(0xFFD32F2F))
          }
        } else {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFFFFEBEE), RoundedCornerShape(12.dp))
              .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "Are you sure? This will reset all 100 levels progress.",
              fontSize = 12.sp,
              color = Color(0xFFC62828)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Button(
                onClick = {
                  onResetProgress()
                  showResetConfirm = false
                  onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
              ) {
                Text("Yes, Reset")
              }
              OutlinedButton(onClick = { showResetConfirm = false }) {
                Text("Cancel")
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun RulesDialog(onDismiss: () -> Unit) {
  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(26.dp),
      color = Color.White,
      shadowElevation = 10.dp,
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("dialog_rules")
    ) {
      Column(
        modifier = Modifier
          .padding(22.dp)
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "HOW TO PLAY",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = GameOrangePrimary
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        RuleStep("1", "Tap Uncovered Foods", "Tap any food item that is not covered by another tile to collect it.")
        RuleStep("2", "6-Slot Tray", "Selected items move into the bottom tray, which has exactly 6 slots.")
        RuleStep("3", "Triple Match Clear", "Collect 3 of the same food items to automatically clear them and free up slots!")
        RuleStep("4", "Winning Condition", "Clear all food tiles from the board to win the level and earn stars and coins.")
        RuleStep("5", "Avoid Tray Overflow", "If all 6 tray slots are full and no triple can be cleared, the game is lost!")
        RuleStep("6", "Power-Ups", "Use Undo (↩️) to reverse a move, Shuffle (🔀) to rearrange tiles, or Hint (💡) to highlight a match.")

        Button(
          onClick = onDismiss,
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
          colors = ButtonDefaults.buttonColors(containerColor = GameOrangePrimary),
          shape = RoundedCornerShape(14.dp)
        ) {
          Text("GOT IT! LET'S PLAY", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun RuleStep(number: String, title: String, description: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalAlignment = Alignment.Top
  ) {
    Surface(
      shape = RoundedCornerShape(8.dp),
      color = Color(0xFFFFE0B2),
      modifier = Modifier.size(26.dp)
    ) {
      Box(contentAlignment = Alignment.Center) {
        Text(number, fontWeight = FontWeight.Bold, color = GameOrangePrimary, fontSize = 13.sp)
      }
    }

    Column(modifier = Modifier.weight(1f)) {
      Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
      Text(description, fontSize = 12.sp, color = Color.Gray, lineHeight = 16.sp)
    }
  }
}
