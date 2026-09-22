package com.pocketbudget.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pocketbudget.app.data.DataBackupManager
import com.pocketbudget.app.data.local.database.AppDatabase
import com.pocketbudget.app.data.repository.CategoryRepository
import com.pocketbudget.app.data.repository.SettingsRepository
import com.pocketbudget.app.ui.theme.AppThemeMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class SettingsEvent {
    data class ShowMessage(val message: String) : SettingsEvent()
    data class ShareExportJson(val jsonContent: String) : SettingsEvent()
}

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val database: AppDatabase,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val themeMode: StateFlow<AppThemeMode> = settingsRepository.themeMode

    private val _eventFlow = MutableSharedFlow<SettingsEvent>()
    val eventFlow: SharedFlow<SettingsEvent> = _eventFlow.asSharedFlow()

    fun setThemeMode(mode: AppThemeMode) {
        settingsRepository.setThemeMode(mode)
    }

    fun exportData() {
        viewModelScope.launch {
            try {
                val json = withContext(Dispatchers.IO) {
                    val categories = database.categoryDao().getAllCategoriesDirect()
                    val transactions = database.transactionDao().getAllTransactionsDirect()
                    val goals = database.goalDao().getAllGoalsDirect()
                    DataBackupManager.exportToJson(categories, transactions, goals)
                }
                _eventFlow.emit(SettingsEvent.ShareExportJson(json))
            } catch (e: Exception) {
                _eventFlow.emit(SettingsEvent.ShowMessage("Failed to export data: ${e.localizedMessage}"))
            }
        }
    }

    fun importData(jsonContent: String) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val backup = DataBackupManager.importFromJson(jsonContent)
                    if (backup.categories.isNotEmpty()) {
                        database.categoryDao().insertCategories(backup.categories)
                    }
                    if (backup.transactions.isNotEmpty()) {
                        database.transactionDao().insertTransactions(backup.transactions)
                    }
                    if (backup.goals.isNotEmpty()) {
                        database.goalDao().insertGoals(backup.goals)
                    }
                }
                _eventFlow.emit(SettingsEvent.ShowMessage("Data imported successfully!"))
            } catch (e: Exception) {
                _eventFlow.emit(SettingsEvent.ShowMessage("Invalid backup file: ${e.localizedMessage}"))
            }
        }
    }

    fun deleteAllData() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    database.transactionDao().deleteAllTransactions()
                    database.goalDao().deleteAllGoals()
                    categoryRepository.checkAndSeedDefaults()
                }
                _eventFlow.emit(SettingsEvent.ShowMessage("All financial data has been deleted."))
            } catch (e: Exception) {
                _eventFlow.emit(SettingsEvent.ShowMessage("Failed to delete data: ${e.localizedMessage}"))
            }
        }
    }

    class Factory(
        private val settingsRepository: SettingsRepository,
        private val database: AppDatabase,
        private val categoryRepository: CategoryRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(settingsRepository, database, categoryRepository) as T
        }
    }
}
