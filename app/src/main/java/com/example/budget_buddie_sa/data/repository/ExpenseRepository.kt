package com.example.budget_buddie_sa.data.repository

import com.example.budget_buddie_sa.data.model.Expense
import java.util.*

/**
 * Repository to handle data operations for Expenses.
 * Following the Repository pattern for Clean Architecture.
 */
class ExpenseRepository {
    
    // In-memory data for now
    private val expenses = mutableListOf<Expense>()

    fun getAllExpenses(): List<Expense> = expenses

    fun addExpense(expense: Expense) {
        expenses.add(expense)
    }

    fun getRecentHistory(): List<Expense> {
        return listOf(
            Expense("1", 450.0, "Grocery", "Pick n Pay", Date()),
            Expense("2", 120.0, "Transport", "Uber", Date()),
            Expense("3", 2000.0, "Rent", "Apartment", Date())
        )
    }
}