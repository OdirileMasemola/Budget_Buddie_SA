package com.example.budget_buddie_sa.data.repository

import com.example.budget_buddie_sa.data.local.BudgetDao
import com.example.budget_buddie_sa.data.model.Budget
import kotlinx.coroutines.flow.Flow

/**
 * Repository to handle data operations for Budget using Room and Firestore.
 */
class BudgetRepository(
    private val budgetDao: BudgetDao,
    private val syncRepository: FirebaseSyncRepository
) {

    fun getBudgetForUser(userId: String): Flow<Budget?> {
        return budgetDao.getBudgetForUser(userId)
    }

    suspend fun insertBudget(budget: Budget) {
        budgetDao.insert(budget)
        syncRepository.syncBudget(budget)
    }

    suspend fun updateBudget(budget: Budget) {
        val updatedBudget = budget.copy(updatedAt = System.currentTimeMillis())
        budgetDao.update(updatedBudget)
        syncRepository.syncBudget(updatedBudget)
    }

    /**
     * Used during login sync to update local RoomDB with Firestore data.
     */
    suspend fun syncFromCloud(budgets: List<Budget>) {
        budgets.forEach { budgetDao.insert(it) }
    }
}
