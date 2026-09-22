package com.pocketbudget.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.pocketbudget.app.ui.navigation.MainAppContainer
import com.pocketbudget.app.ui.theme.PocketBudgetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as PocketBudgetApplication

        setContent {
            val currentTheme by app.settingsRepository.themeMode.collectAsState()
            PocketBudgetTheme(themeMode = currentTheme) {
                MainAppContainer()
            }
        }
    }
}
