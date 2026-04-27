package com.example.budget_buddie_sa.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.budget_buddie_sa.BudgetApp
import com.example.budget_buddie_sa.data.model.Budget
import com.example.budget_buddie_sa.data.model.Expense
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

/**
 * DashboardViewModel manages the data for the Dashboard screen.
 * It observes the database in real-time using Flow and exposes it as LiveData.
 */
class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val expenseRepo = (application as BudgetApp).expenseRepository
    private val budgetRepo = (application as BudgetApp).budgetRepository

    // Observe total spending from the database. 
    // Room will automatically emit a new value whenever the 'expenses' table changes.
    val totalSpendingValue: LiveData<Double?> = expenseRepo.totalSpending.asLiveData()

    // Observe recent expenses (limit to 5 for the dashboard).
    // The map operator allows us to transform the data before it reaches the UI.
    val recentExpenses: LiveData<List<Expense>> = expenseRepo.allExpenses.map { 
        it.take(5) 
    }.asLiveData()

    // Observe the current budget.
    val currentBudget: LiveData<Budget?> = budgetRepo.budget.asLiveData()

    // Calculate remaining budget automatically when either total spending or budget changes.
    // 'combine' merges multiple Flows into one, ensuring the UI stays in sync with all dependencies.
    val remainingBudget: LiveData<Double> = combine(
        expenseRepo.totalSpending,
        budgetRepo.budget
    ) { spending, budget ->
        val total = spending ?: 0.0
        val limit = budget?.maxAmount ?: 0.0
        limit - total
    }.asLiveData()

    // Calculate spending progress percentage (0-100)
    val spendingProgress: LiveData<Int> = combine(
        expenseRepo.totalSpending,
        budgetRepo.budget
    ) { spending, budget ->
        val total = spending ?: 0.0
        val limit = budget?.maxAmount ?: 1.0 // Avoid division by zero
        ((total / limit) * 100).toInt().coerceIn(0, 100)
    }.asLiveData()
}
