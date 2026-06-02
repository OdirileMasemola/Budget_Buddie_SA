package com.example.budget_buddie_sa.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import com.example.budget_buddie_sa.BudgetApp
import com.example.budget_buddie_sa.SessionManager
import com.example.budget_buddie_sa.data.model.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * ViewModel for managing Categories with Cloud Sync support.
 */
class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as BudgetApp).categoryRepository
    private val sessionManager = SessionManager(application)
    private val userId: String = sessionManager.getUserId() ?: ""

    val allCategories: LiveData<List<Category>> = if (userId.isNotEmpty()) {
        repository.getCategoriesForUser(userId).asLiveData()
    } else {
        MutableLiveData(emptyList())
    }

    /**
     * Inserts a new category. Generates a unique String ID for cloud sync.
     */
    fun insertCategory(name: String, color: String, imageUri: Uri? = null) {
        if (userId.isEmpty()) return
        
        val newCategory = Category(
            id = UUID.randomUUID().toString(), // Generate unique ID
            userId = userId,
            name = name,
            color = color
        )

        viewModelScope.launch(Dispatchers.IO) {
            repository.insertCategory(newCategory, imageUri)
        }
    }

    fun updateCategory(category: Category, newImageUri: Uri? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCategory(category, newImageUri)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCategory(category)
        }
    }
}
