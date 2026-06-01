package com.example.budget_buddie_sa.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.budget_buddie_sa.BudgetApp
import com.example.budget_buddie_sa.SessionManager
import com.example.budget_buddie_sa.data.model.Budget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * ViewModel for managing the User's Budget.
 * Updated to use String userId.
 */
class BudgetViewModel(application: Application) : AndroidViewModel(application) {

    private val budgetRepository = (application as BudgetApp).budgetRepository
    private val expenseRepository = (application as BudgetApp).expenseRepository
    private val sessionManager = SessionManager(application)
    private val userId: String = sessionManager.getUserId() ?: ""

    val currentBudget: LiveData<Budget?> = if (userId.isNotEmpty()) {
        budgetRepository.getBudgetForUser(userId).asLiveData()
    } else {
        MutableLiveData(null)
    }
    
    val totalSpent: LiveData<Double?> = if (userId.isNotEmpty()) {
        expenseRepository.getTotalSpendingForUser(userId).asLiveData()
    } else {
        MutableLiveData(0.0)
    }

    // Combined Flow for progress percentage, converted to LiveData
    val spendingProgress: LiveData<Int> = if (userId.isNotEmpty()) {
        combine(
            budgetRepository.getBudgetForUser(userId),
            expenseRepository.getTotalSpendingForUser(userId)
        ) { budget, spent ->
            calculateProgress(budget, spent)
        }.asLiveData()
    } else {
        MutableLiveData(0)
    }

    private fun calculateProgress(budget: Budget?, spent: Double?): Int {
        val max = budget?.maxAmount ?: 0.0
        val actualSpent = spent ?: 0.0
        
        if (max <= 0.0 || actualSpent <= 0.0) return 0
        
        val percentage = (actualSpent / max) * 100
        return percentage.toInt().coerceIn(0, 100)
    }

    fun saveBudget(min: Double, max: Double) {
        if (userId.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            val existingBudget = budgetRepository.getBudgetForUser(userId).firstOrNull()
            val newBudget = if (existingBudget != null) {
                existingBudget.copy(minAmount = min, maxAmount = max)
            } else {
                Budget(userId = userId, minAmount = min, maxAmount = max)
            }
            budgetRepository.insertBudget(newBudget)
        }
    }
}
