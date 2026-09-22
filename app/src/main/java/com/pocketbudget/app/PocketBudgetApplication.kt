package com.pocketbudget.app

import android.app.Application
import com.pocketbudget.app.data.local.database.AppDatabase
import com.pocketbudget.app.data.repository.CategoryRepository
import com.pocketbudget.app.data.repository.GoalRepository
import com.pocketbudget.app.data.repository.TransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PocketBudgetApplication : Application() {

    val database by lazy { AppDatabase.getInstance(this) }
    val categoryRepository by lazy { CategoryRepository(database.categoryDao()) }
    val transactionRepository by lazy { TransactionRepository(database.transactionDao()) }
    val goalRepository by lazy { GoalRepository(database.goalDao()) }
    val settingsRepository by lazy { com.pocketbudget.app.data.repository.SettingsRepository(this) }

    override fun onCreate() {
        super.onCreate()
        // Ensure categories are seeded
        CoroutineScope(Dispatchers.IO).launch {
            categoryRepository.checkAndSeedDefaults()
        }
    }
}
