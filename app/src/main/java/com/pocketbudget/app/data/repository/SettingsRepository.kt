package com.pocketbudget.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.pocketbudget.app.ui.theme.AppThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("pocketbudget_settings", Context.MODE_PRIVATE)

    private val _themeMode = MutableStateFlow(getInitialThemeMode())
    val themeMode: StateFlow<AppThemeMode> = _themeMode.asStateFlow()

    private fun getInitialThemeMode(): AppThemeMode {
        val saved = prefs.getString(KEY_THEME_MODE, AppThemeMode.DARK.name) ?: AppThemeMode.DARK.name
        return try {
            AppThemeMode.valueOf(saved)
        } catch (_: Exception) {
            AppThemeMode.DARK // Dark mode is default
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
        _themeMode.value = mode
    }

    companion object {
        private const val KEY_THEME_MODE = "key_theme_mode"
    }
}
