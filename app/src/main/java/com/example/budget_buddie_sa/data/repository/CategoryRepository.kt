package com.example.budget_buddie_sa.data.repository

import android.net.Uri
import com.example.budget_buddie_sa.data.local.CategoryDao
import com.example.budget_buddie_sa.data.model.Category
import kotlinx.coroutines.flow.Flow

/**
 * Repository to handle data operations for Categories using Room and Firebase.
 */
class CategoryRepository(
    private val categoryDao: CategoryDao,
    private val syncRepository: FirebaseSyncRepository,
    private val storageRepository: StorageRepository
) {

    fun getCategoriesForUser(userId: String): Flow<List<Category>> {
        return categoryDao.getCategoriesForUser(userId)
    }

    suspend fun insertCategory(category: Category, imageUri: Uri? = null) {
        var finalCategory = category
        
        // 1. Upload image to Storage if provided
        if (imageUri != null) {
            val path = "users/${category.userId}/category_images/${category.id}.jpg"
            val downloadUrl = storageRepository.uploadImage(imageUri, path)
            if (downloadUrl != null) {
                finalCategory = finalCategory.copy(imageUri = downloadUrl)
            }
        }

        // 2. Save to Room (Offline-first)
        categoryDao.insert(finalCategory)

        // 3. Sync to Firestore
        syncRepository.syncCategory(finalCategory)
    }

    suspend fun updateCategory(category: Category, newImageUri: Uri? = null) {
        var updatedCategory = category.copy(updatedAt = System.currentTimeMillis())
        
        if (newImageUri != null) {
            val path = "users/${category.userId}/category_images/${category.id}.jpg"
            val downloadUrl = storageRepository.uploadImage(newImageUri, path)
            if (downloadUrl != null) {
                updatedCategory = updatedCategory.copy(imageUri = downloadUrl)
            }
        }

        categoryDao.update(updatedCategory)
        syncRepository.syncCategory(updatedCategory)
    }

    suspend fun deleteCategory(category: Category) {
        categoryDao.delete(category)
        syncRepository.deleteCategory(category.userId, category.id)
    }
    
    suspend fun getCategoryById(id: String): Category? {
        return categoryDao.getCategoryById(id)
    }

    /**
     * Used during login sync to update local RoomDB with Firestore data.
     */
    suspend fun syncFromCloud(categories: List<Category>) {
        categories.forEach { categoryDao.insert(it) }
    }
}
