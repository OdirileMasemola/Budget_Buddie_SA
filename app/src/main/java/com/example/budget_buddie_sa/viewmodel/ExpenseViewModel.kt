package com.example.budget_buddie_sa.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.budget_buddie_sa.BudgetApp
import com.example.budget_buddie_sa.SessionManager
import com.example.budget_buddie_sa.data.model.Category
import com.example.budget_buddie_sa.data.model.Expense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel for managing Expenses
 * Updated to use String userId
 */
class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    
    private val expenseRepository = (application as BudgetApp).expenseRepository
    private val categoryRepository = (application as BudgetApp).categoryRepository
    private val sessionManager = SessionManager(application)
    private val userId: String = sessionManager.getUserId() ?: ""

    val allExpenses: LiveData<List<Expense>> = if (userId.isNotEmpty()) {
        expenseRepository.getExpensesForUser(userId).asLiveData()
    } else {
        MutableLiveData(emptyList())
    }
    
    val allCategories: LiveData<List<Category>> = if (userId.isNotEmpty()) {
        categoryRepository.getCategoriesForUser(userId).asLiveData()
    } else {
        MutableLiveData(emptyList())
    }
    
    val totalSpending: LiveData<Double?> = if (userId.isNotEmpty()) {
        expenseRepository.getTotalSpendingForUser(userId).asLiveData()
    } else {
        MutableLiveData(0.0)
    }

    fun addExpense(expense: Expense, onComplete: () -> Unit) {
        if (userId.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            val expenseToSave = expense.copy(userId = userId)
            expenseRepository.insertExpense(expenseToSave)
            launch(Dispatchers.Main) {
                onComplete()
            }
        }
    }
}
