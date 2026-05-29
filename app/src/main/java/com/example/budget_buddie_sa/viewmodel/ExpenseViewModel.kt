package com.example.budget_buddie_sa.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.budget_buddie_sa.BudgetApp
import com.example.budget_buddie_sa.SessionManager
import com.example.budget_buddie_sa.data.model.Category
import com.example.budget_buddie_sa.data.model.Expense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    
    private val expenseRepository = (application as BudgetApp).expenseRepository
    private val categoryRepository = (application as BudgetApp).categoryRepository
    private val sessionManager = SessionManager(application)
    private val userId = sessionManager.getUserId()

    val allExpenses: LiveData<List<Expense>> = expenseRepository.getExpensesForUser(userId).asLiveData()
    val allCategories: LiveData<List<Category>> = categoryRepository.getCategoriesForUser(userId).asLiveData()
    val totalSpending: LiveData<Double?> = expenseRepository.getTotalSpendingForUser(userId).asLiveData()

    fun addExpense(expense: Expense, onComplete: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            // Ensure userId is set
            val expenseToSave = expense.copy(userId = userId)
            expenseRepository.addExpense(expenseToSave)
            launch(Dispatchers.Main) {
                onComplete()
            }
        }
    }
}
