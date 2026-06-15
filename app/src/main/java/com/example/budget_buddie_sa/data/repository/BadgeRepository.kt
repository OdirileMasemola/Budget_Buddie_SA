package com.example.budget_buddie_sa.data.repository

import android.util.Log
import com.example.budget_buddie_sa.data.local.BadgeDao
import com.example.budget_buddie_sa.data.model.Badge
import com.example.budget_buddie_sa.data.model.Budget
import com.example.budget_buddie_sa.data.model.Category
import com.example.budget_buddie_sa.data.model.Expense
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Repository to handle badge data operations and unlock logic.
 */
class BadgeRepository(
    private val badgeDao: BadgeDao,
    private val syncRepository: FirebaseSyncRepository
) {
    fun getBadgesForUser(userId: String): Flow<List<Badge>> {
        return badgeDao.getBadgesForUser(userId)
    }

    suspend fun insertBadges(badges: List<Badge>) {
        badgeDao.insertBadges(badges)
        badges.forEach { syncRepository.syncBadge(it) }
    }

    suspend fun syncFromCloud(badges: List<Badge>) {
        badgeDao.insertBadges(badges)
    }

    /**
     * Logic to recalculate and unlock badges.
     */
    suspend fun refreshBadges(
        userId: String,
        expenses: List<Expense>,
        budget: Budget?,
        categories: List<Category>
    ) {
        try {
            val currentBadges = badgeDao.getBadgesForUser(userId).first().associateBy { it.badgeId }
            val updatedBadges = mutableListOf<Badge>()

            // Badge 1: Getting Started (Bronze)
            updatedBadges.add(createBadgeObj(
                currentBadges["getting_started"], "getting_started", userId, 
                "Getting Started", "Add your first expense.", "BRONZE", 
                expenses.size.coerceAtMost(1), 1
            ))

            // Badge 2: Expense Tracker (Silver)
            updatedBadges.add(createBadgeObj(
                currentBadges["expense_tracker"], "expense_tracker", userId, 
                "Expense Tracker", "Record 20 expenses.", "SILVER", 
                expenses.size.coerceAtMost(20), 20
            ))

            // Badge 3: Category Explorer (Bronze)
            updatedBadges.add(createBadgeObj(
                currentBadges["category_explorer"], "category_explorer", userId, 
                "Category Explorer", "Create 5 spending categories.", "BRONZE", 
                categories.size.coerceAtMost(5), 5
            ))

            // Badge 4: Savings Master (Gold)
            val isSavingsUnlocked = if (budget != null && budget.maxAmount > 0) {
                val startOfMonth = Calendar.getInstance().apply {
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                
                val monthSpending = expenses.filter { it.date >= startOfMonth }.sumOf { it.amount }
                val percentUsed = (monthSpending / budget.maxAmount * 100).toInt()
                percentUsed < 80 && monthSpending > 0 // Only unlock if they actually spent something but stayed low
            } else false
            
            updatedBadges.add(createBadgeObj(
                currentBadges["savings_master"], "savings_master", userId, 
                "Savings Master", "Spend less than 80% of your budget.", "GOLD", 
                if (isSavingsUnlocked) 1 else 0, 1
            ))

            // Badge 5: Consistency Champion (Gold)
            val streak = calculateConsecutiveDays(expenses)
            updatedBadges.add(createBadgeObj(
                currentBadges["consistency_champion"], "consistency_champion", userId, 
                "Consistency Champion", "Log expenses for 14 consecutive days.", "GOLD", 
                streak.coerceAtMost(14), 14
            ))

            // Badge 6: Budget Keeper (Silver)
            val budgetStreak = calculateBudgetStreak(expenses, budget)
            updatedBadges.add(createBadgeObj(
                currentBadges["budget_keeper"], "budget_keeper", userId, 
                "Budget Keeper", "Stay within your budget for 7 consecutive days.", "SILVER", 
                budgetStreak.coerceAtMost(7), 7
            ))

            insertBadges(updatedBadges)
        } catch (e: Exception) {
            Log.e("BadgeRepository", "Error refreshing badges: ${e.message}")
        }
    }

    private fun createBadgeObj(
        current: Badge?, id: String, userId: String, name: String, 
        desc: String, reward: String, progress: Int, target: Int
    ): Badge {
        val isUnlocked = progress >= target
        val now = System.currentTimeMillis()
        val unlockedDate = if (isUnlocked) (current?.unlockedDate ?: now) else null
        
        return current?.copy(
            currentProgress = progress,
            isUnlocked = isUnlocked,
            unlockedDate = unlockedDate,
            lastUpdated = now
        ) ?: Badge(id, userId, name, desc, reward, isUnlocked, progress, target, unlockedDate, now)
    }

    /**
     * Calculates the current streak of consecutive days with at least one expense.
     * Starts from today or the most recent expense day and goes backwards.
     */
    private fun calculateConsecutiveDays(expenses: List<Expense>): Int {
        if (expenses.isEmpty()) return 0
        
        val expenseDays = expenses.map { truncateDate(it.date) }.distinct().sortedDescending()
        
        val today = truncateDate(System.currentTimeMillis())
        val yesterday = today - TimeUnit.DAYS.toMillis(1)
        
        // If the most recent expense is not today or yesterday, the streak is broken (0).
        if (expenseDays[0] < yesterday) return 0
        
        var streak = 1
        for (i in 0 until expenseDays.size - 1) {
            if (expenseDays[i] - expenseDays[i+1] == TimeUnit.DAYS.toMillis(1)) {
                streak++
            } else {
                break
            }
        }
        return streak
    }

    /**
     * Calculates how many consecutive days (ending today or yesterday) 
     * the user stayed within their daily budget.
     */
    private fun calculateBudgetStreak(expenses: List<Expense>, budget: Budget?): Int {
        if (budget == null || budget.maxAmount <= 0) return 0
        
        val dailyLimit = budget.maxAmount / 30.0
        val dailySpending = expenses.groupBy { truncateDate(it.date) }
            .mapValues { it.value.sumOf { exp -> exp.amount } }
            
        val today = truncateDate(System.currentTimeMillis())
        var streak = 0
        val cal = Calendar.getInstance().apply { timeInMillis = today }
        
        // We check up to the last 30 days to find the current consecutive streak
        for (i in 0 until 30) {
            val daySpending = dailySpending[cal.timeInMillis] ?: 0.0
            if (daySpending <= dailyLimit) {
                streak++
            } else {
                // Streak broken
                if (i == 0) {
                    // If today is over budget, we still check yesterday to see if there was a streak ending then?
                    // Usually streak is "current", so if today is over, streak is 0.
                    return 0
                }
                break
            }
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }

        return streak
    }

    private fun truncateDate(time: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = time
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
}
