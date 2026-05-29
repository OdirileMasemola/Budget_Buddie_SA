package com.example.budget_buddie_sa.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.budget_buddie_sa.BudgetApp
import com.example.budget_buddie_sa.SessionManager
import com.example.budget_buddie_sa.data.model.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as BudgetApp).categoryRepository
    private val sessionManager = SessionManager(application)
    private val userId = sessionManager.getUserId()

    val allCategories: LiveData<List<Category>> = repository.getCategoriesForUser(userId).asLiveData()

    fun insertCategory(category: Category) {
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
