package com.example.budget_buddie_sa.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.budget_buddie_sa.data.local.AppDatabase
import com.example.budget_buddie_sa.data.model.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for managing Categories.
 */
class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val categoryDao = AppDatabase.getDatabase(application).categoryDao()

    // Expose categories as LiveData for easier lifecycle management in Activity
    val allCategories: LiveData<List<Category>> = categoryDao.getAllCategories().asLiveData()

    fun insertCategory(name: String) {
        val newCategory = Category(
            name = name,
            color = "#6200EE",
            iconName = "ic_category"
        )
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                categoryDao.insert(newCategory)
            }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                categoryDao.delete(category)
            }
        }
    }
}
