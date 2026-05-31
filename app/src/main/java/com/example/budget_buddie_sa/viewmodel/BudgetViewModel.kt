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

class BudgetViewModel(application: Application) : AndroidViewModel(application) {

    private val budgetRepository = (application as BudgetApp).budgetRepository
    private val expenseRepository = (application as BudgetApp).expenseRepository
    private val sessionManager = SessionManager(application)
    private val userId = sessionManager.getUserId()

    val currentBudget: LiveData<Budget?> = budgetRepository.getBudgetForUser(userId).asLiveData()
    val totalSpent: LiveData<Double?> = expenseRepository.getTotalSpendingForUser(userId).asLiveData()

    // Combined Flow for progress percentage, converted to LiveData
    val spendingProgress: LiveData<Int> = combine(
        budgetRepository.getBudgetForUser(userId),
        expenseRepository.getTotalSpendingForUser(userId)
    ) { budget, spent ->
        calculateProgress(budget, spent)
    }.asLiveData()

    private fun calculateProgress(budget: Budget?, spent: Double?): Int {
        val max = budget?.maxAmount ?: 0.0
        val actualSpent = spent ?: 0.0
        
        if (max <= 0.0 || actualSpent <= 0.0) return 0
        
        val percentage = (actualSpent / max) * 100
        return percentage.toInt().coerceIn(0, 100)
    }

    fun saveBudget(min: Double, max: Double) {
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
