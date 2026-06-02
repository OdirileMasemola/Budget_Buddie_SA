package com.example.budget_buddie_sa.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import com.example.budget_buddie_sa.BudgetApp
import com.example.budget_buddie_sa.SessionManager
import com.example.budget_buddie_sa.data.model.Category
import com.example.budget_buddie_sa.data.model.Expense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * ViewModel for managing Expenses with Cloud Sync support.
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

    /**
     * Adds a new expense. Generates unique String ID.
     */
    fun addExpense(
        amount: Double,
        date: Long,
        description: String,
        categoryId: String,
        receiptUri: Uri? = null,
        onComplete: () -> Unit
    ) {
        if (userId.isEmpty()) return
        
        val newExpense = Expense(
            id = UUID.randomUUID().toString(),
            userId = userId,
            amount = amount,
            date = date,
            description = description,
            categoryId = categoryId
        )

        viewModelScope.launch(Dispatchers.IO) {
            expenseRepository.insertExpense(newExpense, receiptUri)
            launch(Dispatchers.Main) {
                onComplete()
            }
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch(Dispatchers.IO) {
            expenseRepository.deleteExpense(expense)
        }
    }
}
