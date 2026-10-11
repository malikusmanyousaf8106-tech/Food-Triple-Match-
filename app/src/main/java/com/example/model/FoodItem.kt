package com.example.model

import androidx.compose.ui.graphics.Color

enum class FoodCategory(val displayName: String) {
  FRUIT("Fruit"),
  FAST_FOOD("Fast Food"),
  SWEET("Sweet"),
  VEGGIE("Veggie")
}

enum class FoodType(
  val emoji: String,
  val displayName: String,
  val category: FoodCategory,
  val primaryColor: Color,
  val secondaryColor: Color,
  val borderColor: Color
) {
  APPLE("🍎", "Apple", FoodCategory.FRUIT, Color(0xFFFF5252), Color(0xFFFFEAEA), Color(0xFFD32F2F)),
  BANANA("🍌", "Banana", FoodCategory.FRUIT, Color(0xFFFFD54F), Color(0xFFFFFDE7), Color(0xFFFBC02D)),
  STRAWBERRY("🍓", "Strawberry", FoodCategory.FRUIT, Color(0xFFFF4081), Color(0xFFFCE4EC), Color(0xFFC2185B)),
  ORANGE("🍊", "Orange", FoodCategory.FRUIT, Color(0xFFFF9800), Color(0xFFFFF3E0), Color(0xFFF57C00)),
  GRAPES("🍇", "Grapes", FoodCategory.FRUIT, Color(0xFFAB47BC), Color(0xFFF3E5F5), Color(0xFF7B1FA2)),
  WATERMELON("🍉", "Watermelon", FoodCategory.FRUIT, Color(0xFF4CAF50), Color(0xFFE8F5E9), Color(0xFF388E3C)),
  BURGER("🍔", "Burger", FoodCategory.FAST_FOOD, Color(0xFFFF7043), Color(0xFFFBE9E7), Color(0xFFD84315)),
  PIZZA("🍕", "Pizza", FoodCategory.FAST_FOOD, Color(0xFFFFA726), Color(0xFFFFF8E1), Color(0xFFEF6C00)),
  DONUT("🍩", "Donut", FoodCategory.SWEET, Color(0xFFEC407A), Color(0xFFFCE4EC), Color(0xFFAD1457)),
  CUPCAKE("🧁", "Cupcake", FoodCategory.SWEET, Color(0xFF26A69A), Color(0xFFE0F2F1), Color(0xFF00796B)),
  FRENCH_FRIES("🍟", "Fries", FoodCategory.FAST_FOOD, Color(0xFFFFCA28), Color(0xFFFFF9C4), Color(0xFFFFA000)),
  ICE_CREAM("🍦", "Ice Cream", FoodCategory.SWEET, Color(0xFF42A5F5), Color(0xFFE3F2FD), Color(0xFF1976D2)),
  CARROT("🥕", "Carrot", FoodCategory.VEGGIE, Color(0xFFFF7043), Color(0xFFFFF3E0), Color(0xFFE64A19)),
  CAKE("🍰", "Cake", FoodCategory.SWEET, Color(0xFFFF80AB), Color(0xFFFCE4EC), Color(0xFFC51162)),
  CHERRIES("🍒", "Cherries", FoodCategory.FRUIT, Color(0xFFE91E63), Color(0xFFFFEBEE), Color(0xFF880E4F)),
  AVOCADO("🥑", "Avocado", FoodCategory.VEGGIE, Color(0xFF66BB6A), Color(0xFFE8F8F5), Color(0xFF2E7D32));
}

data class Tile(
  val id: Int,
  val type: FoodType,
  val layer: Int = 0,
  val gridX: Float = 0f,
  val gridY: Float = 0f,
  val isCovered: Boolean = false
)

data class TraySlot(
  val slotIndex: Int,
  val tile: Tile? = null,
  val isMatching: Boolean = false
)
