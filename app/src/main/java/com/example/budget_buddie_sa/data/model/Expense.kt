package com.example.budget_buddie_sa.data.model

import java.util.Date

/**
 * Data model representing an expense.
 */
data class Expense(
    val id: String,
    val amount: Double,
    val category: String,
    val description: String,
    val date: Date,
    val imageUrl: String? = null
)