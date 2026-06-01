package com.example.budget_buddie_sa.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.budget_buddie_sa.BudgetApp
import com.example.budget_buddie_sa.SessionManager
import com.example.budget_buddie_sa.data.model.Budget
import com.example.budget_buddie_sa.data.model.Expense
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.*

/**
 * DashboardViewModel manages the data for the Dashboard screen.
 * Updated to use String userId.
 */
class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val expenseRepo = (application as BudgetApp).expenseRepository
    private val budgetRepo = (application as BudgetApp).budgetRepository
    private val categoryRepo = (application as BudgetApp).categoryRepository
    private val sessionManager = SessionManager(application)
    private val userId: String = sessionManager.getUserId() ?: ""

    // Observe total spending from the database. 
    val totalSpendingValue: LiveData<Double?> = if (userId.isNotEmpty()) {
        expenseRepo.getTotalSpendingForUser(userId).asLiveData()
    } else {
        MutableLiveData(0.0)
    }

    // Observe recent expenses (limit to 5 for the dashboard).
    val recentExpenses: LiveData<List<Expense>> = if (userId.isNotEmpty()) {
        expenseRepo.getExpensesForUser(userId).map { 
            it.take(5) 
        }.asLiveData()
    } else {
        MutableLiveData(emptyList())
    }

    // Observe the current budget.
    val currentBudget: LiveData<Budget?> = if (userId.isNotEmpty()) {
        budgetRepo.getBudgetForUser(userId).asLiveData()
    } else {
        MutableLiveData(null)
    }

    // Calculate remaining budget automatically
    val remainingBudget: LiveData<Double> = if (userId.isNotEmpty()) {
        combine(
            expenseRepo.getTotalSpendingForUser(userId),
            budgetRepo.getBudgetForUser(userId)
        ) { spending, budget ->
            val total = spending ?: 0.0
            val limit = budget?.maxAmount ?: 0.0
            if (limit == 0.0) 0.0 else limit - total
        }.asLiveData()
    } else {
        MutableLiveData(0.0)
    }

    // Real percentage for display (can be > 100)
    val spendingPercentText: LiveData<Int> = if (userId.isNotEmpty()) {
        combine(
            expenseRepo.getTotalSpendingForUser(userId),
            budgetRepo.getBudgetForUser(userId)
        ) { spending, budget ->
            val total = spending ?: 0.0
            val limit = budget?.maxAmount ?: 0.0
            if (limit == 0.0) 0 else ((total / limit) * 100).toInt()
        }.asLiveData()
    } else {
        MutableLiveData(0)
    }

    // Clamped percentage for progress bar (0-100)
    val spendingProgress: LiveData<Int> = spendingPercentText.map { percent ->
        percent.coerceIn(0, 100)
    }

    // This Month total logic
    val thisMonthSpending: LiveData<Double?> = if (userId.isNotEmpty()) {
        flow {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfMonth = calendar.timeInMillis
            
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            val endOfMonth = calendar.timeInMillis
            
            emitAll(expenseRepo.getSpendingForPeriod(userId, startOfMonth, endOfMonth))
        }.asLiveData()
    } else {
        MutableLiveData(0.0)
    }
    
    // Category count for stats
    val categoryCount: LiveData<Int> = if (userId.isNotEmpty()) {
        categoryRepo.getCategoriesForUser(userId).map { it.size }.asLiveData()
    } else {
        MutableLiveData(0)
    }
}
