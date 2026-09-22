package com.pocketbudget.app.data.repository

import com.pocketbudget.app.data.local.dao.CategoryDao
import com.pocketbudget.app.data.local.entity.CategoryEntity
import com.pocketbudget.app.domain.model.Category
import com.pocketbudget.app.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepository(
    private val categoryDao: CategoryDao
) {
    suspend fun checkAndSeedDefaults() {
        if (categoryDao.getCategoryCount() == 0) {
            val entities = Category.allDefaults.map { CategoryEntity.fromDomain(it) }
            categoryDao.insertCategories(entities)
        }
    }

    fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { list ->
            list.map { it.toDomain() }
        }
    }

    fun getCategoriesByType(type: TransactionType): Flow<List<Category>> {
        return categoryDao.getCategoriesByType(type.name).map { list ->
            list.map { it.toDomain() }
        }
    }

    suspend fun insertCategory(category: Category) {
        categoryDao.insertCategory(CategoryEntity.fromDomain(category))
    }
}
