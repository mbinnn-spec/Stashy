package com.pocketbudget.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Home : Screen(
        route = "home",
        title = "Home",
        icon = Icons.Rounded.AccountBalanceWallet
    )

    data object Transactions : Screen(
        route = "transactions",
        title = "History",
        icon = Icons.AutoMirrored.Rounded.ReceiptLong
    )

    data object Goals : Screen(
        route = "goals",
        title = "Goals",
        icon = Icons.Rounded.Flag
    )

    data object Settings : Screen(
        route = "settings",
        title = "Settings",
        icon = Icons.Rounded.Settings
    )

    companion object {
        val bottomNavItems = listOf(Home, Transactions, Goals, Settings)
    }
}
