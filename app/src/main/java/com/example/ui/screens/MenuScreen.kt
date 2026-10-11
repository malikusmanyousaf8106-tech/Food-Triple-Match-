package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.GameCircleButton
import com.example.ui.theme.GameCreamBg
import com.example.ui.theme.GameGoldenAmber
import com.example.ui.theme.GameGoldenYellow
import com.example.ui.theme.GameGreenDark
import com.example.ui.theme.GameGreenPlay
import com.example.ui.theme.GameOrangeDark
import com.example.ui.theme.GameOrangePrimary
import com.example.viewmodel.ActiveDialog
import com.example.viewmodel.AppScreen
import com.example.viewmodel.GameViewModel

@Composable
fun MenuScreen(
  viewModel: GameViewModel,
  modifier: Modifier = Modifier
) {
  val state by viewModel.uiState.collectAsState()

  // Bouncing button animation
  val infiniteTransition = rememberInfiniteTransition(label = "play_bounce")
  val bounceScale by infiniteTransition.animateFloat(
    initialValue = 1.0f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(650, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "play_scale"
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            GameCreamBg,
            Color(0xFFFFF3E0),
            Color(0xFFFFE0B2)
          )
        )
      )
      .statusBarsPadding()
      .navigationBarsPadding()
      .testTag("menu_screen"),
    contentAlignment = Alignment.Center
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .widthIn(max = 500.dp)
        .padding(horizontal = 24.dp, vertical = 12.dp)
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top row: Sound, Coins, Settings
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        GameCircleButton(
          icon = if (state.soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
          contentDesc = "Sound Toggle",
          testTag = "menu_sound_toggle",
          onClick = { viewModel.toggleSound() }
        )

        // Coin Wallet Badge
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = Color.White,
          shadowElevation = 3.dp,
          modifier = Modifier.border(1.5.dp, Color(0xFFFFD54F), RoundedCornerShape(20.dp))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text("🪙", fontSize = 16.sp)
            Text(
              text = "${state.coins}",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = GameGoldenAmber
            )
          }
        }

        GameCircleButton(
          icon = Icons.Default.Settings,
          contentDesc = "Settings",
          testTag = "menu_settings_button",
          onClick = { viewModel.showDialog(ActiveDialog.SETTINGS) }
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Hero Banner Image
      Surface(
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 8.dp,
        modifier = Modifier
          .fillMaxWidth()
          .height(200.dp)
          .border(2.5.dp, Color(0xFFFFCC80), RoundedCornerShape(24.dp))
      ) {
        Image(
          painter = painterResource(id = R.drawable.game_banner),
          contentDescription = "Food Triple Match Banner",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Game Title
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "FOOD TRIPLE MATCH",
          style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.2.sp
          ),
          color = GameOrangeDark,
          textAlign = TextAlign.Center
        )
        Text(
          text = "Puzzle Master 🍔 🍕 🍰",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.SemiBold
          ),
          color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Stats Card
      Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
        shadowElevation = 4.dp,
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, Color(0xFFFFE082), RoundedCornerShape(18.dp))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "UNLOCKED",
              style = MaterialTheme.typography.labelSmall,
              color = Color.Gray
            )
            Text(
              text = "Level ${state.highestUnlockedLevel}",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = GameOrangePrimary
            )
          }

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "TOTAL SCORE",
              style = MaterialTheme.typography.labelSmall,
              color = Color.Gray
            )
            Text(
              text = "${state.totalScore}",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = GameGreenPlay
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Play & Level Select Buttons
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Big Play Button
        Button(
          onClick = { viewModel.startLevel(state.highestUnlockedLevel) },
          modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .scale(bounceScale)
            .shadow(6.dp, RoundedCornerShape(20.dp))
            .testTag("menu_play_button"),
          colors = ButtonDefaults.buttonColors(containerColor = GameGreenPlay),
          shape = RoundedCornerShape(20.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.PlayArrow,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(28.dp)
            )
            Text(
              text = if (state.highestUnlockedLevel == 1) "PLAY GAME" else "CONTINUE LEVEL ${state.highestUnlockedLevel}",
              fontSize = 18.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Color.White
            )
          }
        }

        // Level Select Button
        OutlinedButton(
          onClick = { viewModel.navigateTo(AppScreen.LEVEL_SELECT) },
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("menu_level_select_button"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = GameOrangePrimary)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(Icons.Default.GridView, contentDescription = null)
            Text(
              text = "SELECT LEVEL (100 Levels)",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        // How to play button
        OutlinedButton(
          onClick = { viewModel.showDialog(ActiveDialog.RULES) },
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .testTag("menu_rules_button"),
          shape = RoundedCornerShape(14.dp)
        ) {
          Text(
            text = "📖 How To Play",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))
    }
  }
}
