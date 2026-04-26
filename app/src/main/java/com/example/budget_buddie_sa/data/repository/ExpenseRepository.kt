package com.example.budget_buddie_sa.data.repository

import com.example.budget_buddie_sa.data.local.ExpenseDao
import com.example.budget_buddie_sa.data.model.Expense
import kotlinx.coroutines.flow.Flow

/**
 * Repository to handle data operations for Expenses using Room.
 */
class ExpenseRepository(private val expenseDao: ExpenseDao) {

    val allExpenses: Flow<List<Expense>> = expenseDao.getAllExpenses()
    val totalSpending: Flow<Double?> = expenseDao.getTotalSpending()

    suspend fun addExpense(expense: Expense) {
        expenseDao.insertExpense(expense)
    }

    suspend fun deleteExpense(expense: Expense) {
        expenseDao.deleteExpense(expense)
    }
}
