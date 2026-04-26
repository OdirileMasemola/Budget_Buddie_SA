package com.example.budget_buddie_sa.repository

import com.example.budget_buddie_sa.data.model.Expense
import java.util.*

/**
 * Repository to handle data operations for Expenses.
 */
class ExpenseRepository {
    
    // In-memory data for now, would be replaced by a local DB (Room) or API (Retrofit)
    private val expenses = mutableListOf<Expense>()

    fun getAllExpenses(): List<Expense> = expenses

    fun addExpense(expense: Expense) {
        expenses.add(expense)
    }

    fun getDummyHistory(): List<Expense> {
        return listOf(
            Expense(amount = 450.0, date = Date().time, description = "Pick n Pay", categoryId = 1),
            Expense(amount = 120.0, date = Date().time, description = "Uber", categoryId = 2),
            Expense(amount = 2000.0, date = Date().time, description = "Apartment", categoryId = 3)
        )
    }
}