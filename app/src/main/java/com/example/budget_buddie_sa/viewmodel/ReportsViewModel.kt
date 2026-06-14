package com.example.budget_buddie_sa.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.budget_buddie_sa.BudgetApp
import com.example.budget_buddie_sa.SessionManager
import com.example.budget_buddie_sa.data.model.Category
import com.example.budget_buddie_sa.data.model.Expense
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

/**
 * Data class to hold calculated report data.
 */
data class ReportData(
    val startDate: Long,
    val endDate: Long,
    val totalSpending: Double = 0.0,
    val totalCategoriesUsed: Int = 0,
    val highestSpendingCategory: String = "N/A",
    val lowestSpendingCategory: String = "N/A",
    val numberOfExpensesRecorded: Int = 0,
    val averageDailySpending: Double = 0.0,
    val categoryBreakdown: List<Pair<Category, Double>> = emptyList(),
    val expenseList: List<Expense> = emptyList()
)

class ReportsViewModel(application: Application) : AndroidViewModel(application) {

    private val budgetApp = application as BudgetApp
    private val expenseRepo = budgetApp.expenseRepository
    private val categoryRepo = budgetApp.categoryRepository
    private val sessionManager = SessionManager(application)
    private val userId = sessionManager.getUserId() ?: ""

    private val _reportData = MutableLiveData<ReportData?>()
    val reportData: LiveData<ReportData?> = _reportData

    fun generateReport(startDate: Long, endDate: Long) {
        if (userId.isEmpty()) return

        viewModelScope.launch {
            // Fetch all categories for mapping
            val categories = categoryRepo.getCategoriesForUser(userId).first()
            val categoryMap = categories.associateBy { it.id }

            // Fetch expenses in range
            val expenses = expenseRepo.getExpensesForUserInPeriod(userId, startDate, endDate).first()

            if (expenses.isEmpty()) {
                _reportData.postValue(null)
                return@launch
            }

            val totalSpending = expenses.sumOf { it.amount }
            val expenseByCategory = expenses.groupBy { it.categoryId }
            val totalCategoriesUsed = expenseByCategory.size
            
            val categoryTotals = expenseByCategory.mapValues { it.value.sumOf { exp -> exp.amount } }
            
            val highestEntry = categoryTotals.maxByOrNull { it.value }
            val lowestEntry = categoryTotals.minByOrNull { it.value }
            
            val highestSpendingCategory = highestEntry?.let { categoryMap[it.key]?.name ?: "Other" } ?: "N/A"
            val lowestSpendingCategory = lowestEntry?.let { categoryMap[it.key]?.name ?: "Other" } ?: "N/A"
            
            val numberOfExpensesRecorded = expenses.size
            
            // Calculate days in range correctly
            val diffInMillis = endDate - startDate
            val daysInRange = (TimeUnit.MILLISECONDS.toDays(diffInMillis) + 1).coerceAtLeast(1)
            val averageDailySpending = totalSpending / daysInRange

            val breakdown = categoryTotals.map { (catId, amount) ->
                (categoryMap[catId] ?: Category(catId, userId, "Other")) to amount
            }.sortedByDescending { it.second }

            _reportData.postValue(ReportData(
                startDate = startDate,
                endDate = endDate,
                totalSpending = totalSpending,
                totalCategoriesUsed = totalCategoriesUsed,
                highestSpendingCategory = highestSpendingCategory,
                lowestSpendingCategory = lowestSpendingCategory,
                numberOfExpensesRecorded = numberOfExpensesRecorded,
                averageDailySpending = averageDailySpending,
                categoryBreakdown = breakdown,
                expenseList = expenses.sortedByDescending { it.date }
            ))
        }
    }
}
