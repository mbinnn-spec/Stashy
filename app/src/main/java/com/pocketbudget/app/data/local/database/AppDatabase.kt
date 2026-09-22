package com.pocketbudget.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pocketbudget.app.data.local.dao.CategoryDao
import com.pocketbudget.app.data.local.dao.GoalDao
import com.pocketbudget.app.data.local.dao.TransactionDao
import com.pocketbudget.app.data.local.entity.CategoryEntity
import com.pocketbudget.app.data.local.entity.GoalEntity
import com.pocketbudget.app.data.local.entity.TransactionEntity
import com.pocketbudget.app.domain.model.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CategoryEntity::class,
        TransactionEntity::class,
        GoalEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun goalDao(): GoalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pocketbudget.db"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Prepopulate default preset categories
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        prepopulateCategories(database.categoryDao())
                    }
                }
            }

            private suspend fun prepopulateCategories(categoryDao: CategoryDao) {
                val defaultEntities = Category.allDefaults.map { CategoryEntity.fromDomain(it) }
                categoryDao.insertCategories(defaultEntities)
            }
        }
    }
}
