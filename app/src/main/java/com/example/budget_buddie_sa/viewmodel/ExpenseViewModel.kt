package com.example.budget_buddie_sa.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.budget_buddie_sa.BudgetApp
import com.example.budget_buddie_sa.data.model.Category
import com.example.budget_buddie_sa.data.model.Expense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = (application as BudgetApp).expenseRepository
    private val categoryDao = (application as BudgetApp).database.categoryDao()

    val allExpenses: LiveData<List<Expense>> = repository.allExpenses.asLiveData()
    val allCategories: LiveData<List<Category>> = categoryDao.getAllCategories().asLiveData()

    fun addExpense(expense: Expense, onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addExpense(expense)
            launch(Dispatchers.Main) {
                onComplete()
            }
        }
    }
}
