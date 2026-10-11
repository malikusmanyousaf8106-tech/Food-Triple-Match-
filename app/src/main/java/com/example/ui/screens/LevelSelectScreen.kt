package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.GameCreamBg
import com.example.ui.theme.GameGoldenAmber
import com.example.ui.theme.GameGoldenYellow
import com.example.ui.theme.GameGreenPlay
import com.example.ui.theme.GameOrangePrimary
import com.example.viewmodel.AppScreen
import com.example.viewmodel.GameViewModel

@Composable
fun LevelSelectScreen(
  viewModel: GameViewModel,
  modifier: Modifier = Modifier
) {
  val state by viewModel.uiState.collectAsState()

  BackHandler {
    viewModel.navigateTo(AppScreen.MENU)
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            GameCreamBg,
            Color(0xFFFFF3E0),
            Color(0xFFFFE8D6)
          )
        )
      )
      .statusBarsPadding()
      .navigationBarsPadding()
      .testTag("level_select_screen")
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          IconButton(
            onClick = { viewModel.navigateTo(AppScreen.MENU) },
            modifier = Modifier
              .size(42.dp)
              .shadow(2.dp, CircleShape)
              .clip(CircleShape)
              .background(Color.White)
              .testTag("level_select_back_button")
          ) {
            Icon(
              imageVector = Icons.Default.ArrowBack,
              contentDescription = "Back",
              tint = GameOrangePrimary
            )
          }

          Text(
            text = "SELECT LEVEL",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.1.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
          )
        }

        // Coins Badge
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = Color.White,
          shadowElevation = 2.dp,
          modifier = Modifier.border(1.dp, Color(0xFFFFD54F), RoundedCornerShape(16.dp))
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Text("🪙", fontSize = 14.sp)
            Text(
              text = "${state.coins}",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = GameGoldenAmber
            )
          }
        }
      }

      // 100 Levels Grid
      LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 74.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
          .fillMaxSize()
          .testTag("levels_grid")
      ) {
        items(100) { index ->
          val level = index + 1
          val isUnlocked = level <= state.highestUnlockedLevel
          val stars = viewModel.getStarsForLevel(level)

          LevelCard(
            levelNumber = level,
            isUnlocked = isUnlocked,
            stars = stars,
            isCurrent = level == state.highestUnlockedLevel,
            onClick = {
              if (isUnlocked) {
                viewModel.startLevel(level)
              }
            }
          )
        }
      }
    }
  }
}

@Composable
fun LevelCard(
  levelNumber: Int,
  isUnlocked: Boolean,
  stars: Int,
  isCurrent: Boolean,
  onClick: () -> Unit
) {
  val shape = RoundedCornerShape(16.dp)

  Surface(
    shape = shape,
    color = when {
      !isUnlocked -> Color(0xFFEEEEEE)
      isCurrent -> Color(0xFFFFF8E1)
      stars > 0 -> Color.White
      else -> Color.White
    },
    shadowElevation = if (isUnlocked) 4.dp else 1.dp,
    modifier = Modifier
      .size(76.dp)
      .clip(shape)
      .border(
        width = if (isCurrent) 2.5.dp else 1.dp,
        color = when {
          !isUnlocked -> Color(0xFFBDBDBD)
          isCurrent -> GameOrangePrimary
          stars > 0 -> Color(0xFFFFD54F)
          else -> Color(0xFFE0E0E0)
        },
        shape = shape
      )
      .clickable(enabled = isUnlocked, onClick = onClick)
      .testTag("level_card_$levelNumber")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(6.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      if (!isUnlocked) {
        Icon(
          imageVector = Icons.Default.Lock,
          contentDescription = "Locked",
          tint = Color(0xFF9E9E9E),
          modifier = Modifier.size(20.dp)
        )
        Text(
          text = "$levelNumber",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = Color(0xFF9E9E9E)
        )
      } else {
        Text(
          text = "$levelNumber",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = if (isCurrent) GameOrangePrimary else MaterialTheme.colorScheme.onBackground
        )

        // Star rating
        Row(
          horizontalArrangement = Arrangement.spacedBy(2.dp),
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(top = 2.dp)
        ) {
          for (s in 1..3) {
            val starEarned = s <= stars
            Text(
              text = if (starEarned) "★" else "☆",
              fontSize = 11.sp,
              color = if (starEarned) GameGoldenAmber else Color(0xFFBDBDBD)
            )
          }
        }
      }
    }
  }
}
