package com.example.budget_buddie_sa.data.repository

import com.example.budget_buddie_sa.data.local.ExpenseDao
import com.example.budget_buddie_sa.data.model.Expense
import kotlinx.coroutines.flow.Flow

/**
 * Repository to handle data operations for Expenses using Room.
 */
class ExpenseRepository(private val expenseDao: ExpenseDao) {

    fun getExpensesForUser(userId: Int): Flow<List<Expense>> {
        return expenseDao.getExpensesForUser(userId)
    }

    fun getTotalSpendingForUser(userId: Int): Flow<Double?> {
        return expenseDao.getTotalSpendingForUser(userId)
    }

    fun getSpendingForPeriod(userId: Int, startDate: Long, endDate: Long): Flow<Double?> {
        return expenseDao.getSpendingForPeriod(userId, startDate, endDate)
    }

    suspend fun addExpense(expense: Expense) {
        expenseDao.insertExpense(expense)
    }

    suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense)
    }
}
