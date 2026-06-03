package com.example.budget_buddie_sa.data.repository

import android.net.Uri
import android.util.Log
import com.example.budget_buddie_sa.data.local.CategoryDao
import com.example.budget_buddie_sa.data.model.Category
import kotlinx.coroutines.flow.Flow

/**
 * Repository to handle data operations for Categories using Room and Firebase.
 */
class CategoryRepository(
    private val categoryDao: CategoryDao,
    private val syncRepository: FirebaseSyncRepository,
    private val localImageRepository: LocalImageRepository
) {

    fun getCategoriesForUser(userId: String): Flow<List<Category>> {
        return categoryDao.getCategoriesForUser(userId)
    }

    suspend fun insertCategory(category: Category, imageUri: Uri? = null) {
        var finalCategory = category
        
        // 1. Save image to internal storage if provided
        if (imageUri != null) {
            val fileName = "category_${category.id}.jpg"
            Log.d("CategoryRepository", "Saving image locally from: $imageUri")
            val localPath = localImageRepository.saveImageToInternalStorage(imageUri, fileName)
            if (localPath != null) {
                Log.d("CategoryRepository", "Image saved locally. Path: $localPath")
                finalCategory = finalCategory.copy(imageUrl = localPath)
            } else {
                Log.e("CategoryRepository", "Local image save failed.")
            }
        }

        Log.d("CategoryRepository", "Saving category to Room & Firestore: ${finalCategory.name}, imageUrl: ${finalCategory.imageUrl}")
        // 2. Save to Room (Offline-first)
        categoryDao.insert(finalCategory)

        // 3. Sync to Firestore (without image path if desired, or keep it as local path for this device)
        // User requested to save it as imageUrl/imageUri only for local use.
        // We still sync the category object, which will include the local path.
        syncRepository.syncCategory(finalCategory)
    }

    suspend fun updateCategory(category: Category, newImageUri: Uri? = null) {
        var updatedCategory = category.copy(updatedAt = System.currentTimeMillis())
        
        if (newImageUri != null) {
            val fileName = "category_${category.id}.jpg"
            Log.d("CategoryRepository", "Updating local image from: $newImageUri")
            val localPath = localImageRepository.saveImageToInternalStorage(newImageUri, fileName)
            if (localPath != null) {
                Log.d("CategoryRepository", "Image updated locally. Path: $localPath")
                updatedCategory = updatedCategory.copy(imageUrl = localPath)
            } else {
                Log.e("CategoryRepository", "Local image update failed.")
            }
        }

        Log.d("CategoryRepository", "Updating category in Room & Firestore: ${updatedCategory.name}, imageUrl: ${updatedCategory.imageUrl}")
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
