package com.example.budget_buddie_sa.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.budget_buddie_sa.BudgetApp
import com.example.budget_buddie_sa.SessionManager
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
    private val categoryRepo = (application as BudgetApp).categoryRepository
    private val sessionManager = SessionManager(application)
    private val userId = sessionManager.getUserId()

    // Observe total spending from the database. 
    val totalSpendingValue: LiveData<Double?> = expenseRepo.getTotalSpendingForUser(userId).asLiveData()

    // Observe recent expenses (limit to 5 for the dashboard).
    val recentExpenses: LiveData<List<Expense>> = expenseRepo.getExpensesForUser(userId).map { 
        it.take(5) 
    }.asLiveData()

    // Observe the current budget.
    val currentBudget: LiveData<Budget?> = budgetRepo.getBudgetForUser(userId).asLiveData()

    // Calculate remaining budget automatically
    val remainingBudget: LiveData<Double> = combine(
        expenseRepo.getTotalSpendingForUser(userId),
        budgetRepo.getBudgetForUser(userId)
    ) { spending, budget ->
        val total = spending ?: 0.0
        val limit = budget?.maxAmount ?: 0.0
        limit - total
    }.asLiveData()

    // Calculate spending progress percentage (0-100)
    val spendingProgress: LiveData<Int> = combine(
        expenseRepo.getTotalSpendingForUser(userId),
        budgetRepo.getBudgetForUser(userId)
    ) { spending, budget ->
        val total = spending ?: 0.0
        val limit = budget?.maxAmount ?: 1.0 // Avoid division by zero
        ((total / limit) * 100).toInt().coerceIn(0, 100)
    }.asLiveData()
    
    // Category count for stats
    val categoryCount: LiveData<Int> = categoryRepo.getCategoriesForUser(userId).map { it.size }.asLiveData()
}
