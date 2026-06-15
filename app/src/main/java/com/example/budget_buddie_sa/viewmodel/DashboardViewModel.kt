package com.example.budget_buddie_sa.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.budget_buddie_sa.BudgetApp
import com.example.budget_buddie_sa.SessionManager
import com.example.budget_buddie_sa.data.model.Badge
import com.example.budget_buddie_sa.data.model.Budget
import com.example.budget_buddie_sa.data.model.Category
import com.example.budget_buddie_sa.data.model.Expense
import kotlinx.coroutines.flow.*
import java.util.*

/**
 * Data class for Budget Analysis result.
 */
data class BudgetAnalysis(
    val limit: Double = 0.0,
    val spent: Double = 0.0,
    val remaining: Double = 0.0,
    val usagePercent: Int = 0,
    val hasBudget: Boolean = false
)

/**
 * Data class for Dashboard Statistics.
 */
data class DashboardStats(
    val totalSpending: Double = 0.0,
    val expenseCount: Int = 0,
    val highestCategory: String = "N/A",
    val lowestCategory: String = "N/A",
    val averageDaily: Double = 0.0,
    val mostRecentExpense: String = "N/A"
)

/**
 * Enum for Date Range selection.
 */
enum class DateRangeType {
    TODAY, LAST_7_DAYS, LAST_30_DAYS, CUSTOM
}

/**
 * DashboardViewModel manages the data for the Dashboard screen.
 */
class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val budgetApp = application as BudgetApp
    private val expenseRepo = budgetApp.expenseRepository
    private val budgetRepo = budgetApp.budgetRepository
    private val categoryRepo = budgetApp.categoryRepository
    
    private val sessionManager = SessionManager(application)
    private val userId: String = sessionManager.getUserId() ?: ""

    // --- Filter State ---
    private val _dateRangeType = MutableStateFlow(DateRangeType.LAST_7_DAYS)
    val dateRangeType: StateFlow<DateRangeType> = _dateRangeType

    private val _customDateRange = MutableStateFlow<Pair<Long, Long>?>(null)
    val customDateRange: StateFlow<Pair<Long, Long>?> = _customDateRange

    // Combined start and end times based on selection
    val activeTimeRange: Flow<Pair<Long, Long>> = combine(_dateRangeType, _customDateRange) { type, custom ->
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        
        val startTime = when (type) {
            DateRangeType.TODAY -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.timeInMillis
            }
            DateRangeType.LAST_7_DAYS -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                calendar.timeInMillis
            }
            DateRangeType.LAST_30_DAYS -> {
                calendar.add(Calendar.DAY_OF_YEAR, -30)
                calendar.timeInMillis
            }
            DateRangeType.CUSTOM -> custom?.first ?: (endTime - 7 * 24 * 60 * 60 * 1000L)
        }
        
        val finalEnd = if (type == DateRangeType.CUSTOM) custom?.second ?: endTime else endTime
        Log.d("DashboardViewModel", "Date range changed: type=$type, start=$startTime, end=$finalEnd")
        Pair(startTime, finalEnd)
    }

    // --- Filtered Data ---
    
    // Fetch all categories once to map IDs to names
    private val allCategories = if (userId.isNotEmpty()) {
        categoryRepo.getCategoriesForUser(userId)
    } else {
        flowOf(emptyList())
    }

    // Fetch filtered expenses based on time range
    val filteredExpenses: LiveData<List<Expense>> = activeTimeRange.flatMapLatest { range ->
        if (userId.isNotEmpty()) {
            expenseRepo.getExpensesForUserInPeriod(userId, range.first, range.second)
        } else {
            flowOf(emptyList())
        }
    }.onEach { Log.d("DashboardViewModel", "Fetched filtered expenses count: ${it.size}") }
    .asLiveData()

    // Group expenses by category for Pie Chart
    val categoryTotals: LiveData<Map<Category, Double>> = combine(filteredExpenses.asFlow(), allCategories) { expenses, categories ->
        val categoryMap = categories.associateBy { it.id }
        val totals = expenses.groupBy { it.categoryId }
            .mapNotNull { (catId, list) ->
                val category = categoryMap[catId] ?: Category(catId, userId, "Other")
                category to list.sumOf { it.amount }
            }.toMap()
        Log.d("DashboardViewModel", "Calculated category totals: ${totals.size} categories")
        totals
    }.asLiveData()

    // Budget Analysis
    val budgetAnalysis: LiveData<BudgetAnalysis> = combine(filteredExpenses.asFlow(), budgetRepo.getBudgetForUser(userId)) { expenses, budget ->
        val totalSpent = expenses.sumOf { it.amount }
        val analysis = if (budget != null) {
            val limit = budget.maxAmount
            val remaining = if (limit == 0.0) 0.0 else limit - totalSpent
            val percent = if (limit == 0.0) 0 else ((totalSpent / limit) * 100).toInt()
            BudgetAnalysis(limit, totalSpent, remaining, percent, true)
        } else {
            BudgetAnalysis(spent = totalSpent, hasBudget = false)
        }
        Log.d("DashboardViewModel", "Fetched budget: ${budget?.maxAmount ?: 0.0}, total spending: $totalSpent")
        analysis
    }.asLiveData()

    // Dashboard Statistics
    val dashboardStats: LiveData<DashboardStats> = combine(filteredExpenses.asFlow(), allCategories) { expenses, categories ->
        if (expenses.isEmpty()) return@combine DashboardStats()
        
        val totalSpent = expenses.sumOf { it.amount }
        val categoryMap = categories.associateBy { it.id }
        
        val categorySpending = expenses.groupBy { it.categoryId }
            .mapValues { it.value.sumOf { amount -> amount.amount } }
        
        val highestEntry = categorySpending.maxByOrNull { it.value }
        val lowestEntry = categorySpending.minByOrNull { it.value }
        
        val highestName = highestEntry?.let { categoryMap[it.key]?.name ?: "Other" } ?: "N/A"
        val lowestName = lowestEntry?.let { categoryMap[it.key]?.name ?: "Other" } ?: "N/A"
        
        // Average Daily Spending
        val days = expenses.groupBy { 
            val cal = Calendar.getInstance().apply { timeInMillis = it.date }
            "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.DAY_OF_YEAR)}"
        }.size.coerceAtLeast(1)
        
        val mostRecent = expenses.maxByOrNull { it.date }?.description ?: "N/A"
        
        DashboardStats(
            totalSpending = totalSpent,
            expenseCount = expenses.size,
            highestCategory = highestName,
            lowestCategory = lowestName,
            averageDaily = totalSpent / days,
            mostRecentExpense = mostRecent
        )
    }.asLiveData()

    // --- Methods ---
    fun setDateRangeType(type: DateRangeType) {
        _dateRangeType.value = type
    }

    fun setCustomDateRange(start: Long, end: Long) {
        _customDateRange.value = Pair(start, end)
        _dateRangeType.value = DateRangeType.CUSTOM
    }

    // --- Existing Dashboard Data (Summary) ---
    // Keep these for the top summary card which shows OVERALL stats usually, 
    // or update them to reflect the filtered range if desired. 
    // Requirement 3 says "Refresh dashboard statistics ... budget analysis". 
    // I will keep the overall ones but add the filtered ones for the analysis section.

    val overallTotalSpending: LiveData<Double?> = if (userId.isNotEmpty()) {
        expenseRepo.getTotalSpendingForUser(userId).asLiveData()
    } else {
        MutableLiveData(0.0)
    }

    val recentExpenses: LiveData<List<Expense>> = if (userId.isNotEmpty()) {
        expenseRepo.getExpensesForUser(userId).map { list ->
            list.take(5)
        }.asLiveData()
    } else {
        MutableLiveData(emptyList())
    }

    val currentBudget: LiveData<Budget?> = if (userId.isNotEmpty()) {
        budgetRepo.getBudgetForUser(userId).asLiveData()
    } else {
        MutableLiveData(null)
    }

    // --- Added back for top summary card compatibility ---
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

    val spendingProgress: LiveData<Int> = spendingPercentText.map { percent ->
        percent.coerceIn(0, 100)
    }
    
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
    // -------------------------------------------------------
    
    val categoryCount: LiveData<Int> = if (userId.isNotEmpty()) {
        categoryRepo.getCategoriesForUser(userId).map { it.size }.asLiveData()
    } else {
        MutableLiveData(0)
    }

    // --- Badge Data ---
    private val badgeRepo = budgetApp.badgeRepository
    
    val highestUnlockedBadge: LiveData<Badge?> = if (userId.isNotEmpty()) {
        badgeRepo.getBadgesForUser(userId).map { badges ->
            badges.filter { it.isUnlocked }
                .sortedWith(compareByDescending<Badge> { 
                    when (it.rewardType) {
                        "GOLD" -> 3
                        "SILVER" -> 2
                        "BRONZE" -> 1
                        else -> 0
                    }
                }.thenByDescending { it.unlockedDate ?: 0L })
                .firstOrNull()
        }.asLiveData()
    } else {
        MutableLiveData(null)
    }
}
