package com.pocketbudget.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Payments
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

object CategoryIconHelper {

    fun getIcon(iconName: String): ImageVector {
        return when (iconName) {
            "AccountBalance" -> Icons.Rounded.AccountBalance
            "Payments" -> Icons.Rounded.Payments
            "CardGiftcard" -> Icons.Rounded.CardGiftcard
            "Restaurant" -> Icons.Rounded.Restaurant
            "DirectionsCar" -> Icons.Rounded.DirectionsCar
            "Wifi" -> Icons.Rounded.Wifi
            "SportsEsports" -> Icons.Rounded.SportsEsports
            "ShoppingBag" -> Icons.Rounded.ShoppingBag
            "School" -> Icons.Rounded.School
            "Movie" -> Icons.Rounded.Movie
            "MoreHoriz" -> Icons.Rounded.MoreHoriz
            else -> Icons.Rounded.Category
        }
    }

    fun parseColor(hex: String, fallback: Color = Color(0xFF5B82A6)): Color {
        return try {
            val cleanHex = hex.replace("#", "")
            val colorLong = cleanHex.toLong(16)
            if (cleanHex.length == 6) {
                Color(0xFF000000 or colorLong)
            } else {
                Color(colorLong)
            }
        } catch (_: Exception) {
            fallback
        }
    }
}
