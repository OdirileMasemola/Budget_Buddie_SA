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
import java.util.UUID

/**
 * ViewModel for managing the User's Budget with Cloud Sync support.
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

    /**
     * Saves or updates the budget. Generates unique String ID for new budgets.
     */
    fun saveBudget(min: Double, max: Double) {
        if (userId.isEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
            val existingBudget = budgetRepository.getBudgetForUser(userId).firstOrNull()
            if (existingBudget != null) {
                val updatedBudget = existingBudget.copy(minAmount = min, maxAmount = max)
                budgetRepository.updateBudget(updatedBudget)
            } else {
                val newBudget = Budget(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    minAmount = min,
                    maxAmount = max
                )
                budgetRepository.insertBudget(newBudget)
            }
        }
    }
}
