package com.example.budget_buddie_sa.data.repository

import com.example.budget_buddie_sa.data.local.BudgetDao
import com.example.budget_buddie_sa.data.model.Budget
import kotlinx.coroutines.flow.Flow

/**
 * Repository to handle data operations for Budget using Room.
 */
class BudgetRepository(private val budgetDao: BudgetDao) {

    fun getBudgetForUser(userId: String): Flow<Budget?> {
        return budgetDao.getBudgetForUser(userId)
    }

    suspend fun insertBudget(budget: Budget) {
        budgetDao.insert(budget)
    }

    suspend fun updateBudget(budget: Budget) {
        budgetDao.update(budget)
    }
}
