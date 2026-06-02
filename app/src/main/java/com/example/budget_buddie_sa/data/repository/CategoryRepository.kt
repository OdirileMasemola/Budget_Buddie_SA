package com.example.budget_buddie_sa.data.repository

import com.example.budget_buddie_sa.data.local.CategoryDao
import com.example.budget_buddie_sa.data.model.Category
import kotlinx.coroutines.flow.Flow

/**
 * Repository to handle data operations for Categories using Room.
 */
class CategoryRepository(private val categoryDao: CategoryDao) {

    fun getCategoriesForUser(userId: String): Flow<List<Category>> {
        return categoryDao.getCategoriesForUser(userId)
    }

    suspend fun insertCategory(category: Category) {
        categoryDao.insert(category)
    }

    suspend fun updateCategory(category: Category) {
        categoryDao.update(category)
    }

    suspend fun deleteCategory(category: Category) {
        categoryDao.delete(category)
    }
    
    suspend fun getCategoryById(id: Int): Category? {
        return categoryDao.getCategoryById(id)
    }
}
