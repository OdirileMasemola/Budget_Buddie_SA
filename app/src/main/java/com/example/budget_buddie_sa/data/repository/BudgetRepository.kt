package com.example.budget_buddie_sa.data.repository

import com.example.budget_buddie_sa.data.local.BudgetDao
import com.example.budget_buddie_sa.data.model.Budget
import kotlinx.coroutines.flow.Flow

class BudgetRepository(private val budgetDao: BudgetDao) {
    val budget: Flow<Budget?> = budgetDao.getBudget()

    suspend fun insert(budget: Budget) {
        budgetDao.insert(budget)
    }

    suspend fun update(budget: Budget) {
        budgetDao.update(budget)
    }
}
