package com.example.budget_buddie_sa.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.budget_buddie_sa.BudgetApp
import com.example.budget_buddie_sa.SessionManager
import com.example.budget_buddie_sa.data.model.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel for managing Categories.
 * Updated to use String userId.
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

    fun insertCategory(category: Category) {
        if (userId.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertCategory(category.copy(userId = userId))
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCategory(category)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCategory(category)
        }
    }
}
