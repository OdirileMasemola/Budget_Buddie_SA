package com.example.budget_buddie_sa.data.repository

import com.example.budget_buddie_sa.data.local.ExpenseDao
import com.example.budget_buddie_sa.data.model.Expense
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val expenseDao: ExpenseDao) {

    fun getExpensesForUser(userId: String): Flow<List<Expense>> {
        return expenseDao.getExpensesForUser(userId)
    }

    suspend fun insertExpense(expense: Expense) {
        expenseDao.insertExpense(expense)
    }

    suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense)
    }

    fun getTotalSpendingForUser(userId: String): Flow<Double?> {
        return expenseDao.getTotalSpendingForUser(userId)
    }

    fun getSpendingForPeriod(userId: String, startDate: Long, endDate: Long): Flow<Double?> {
        return expenseDao.getSpendingForPeriod(userId, startDate, endDate)
    }
}
