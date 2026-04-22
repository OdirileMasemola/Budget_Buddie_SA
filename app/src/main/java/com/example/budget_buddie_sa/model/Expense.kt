package com.example.budget_buddie_sa.model

import java.util.Date

data class Expense(
    val id: String,
    val amount: Double,
    val category: String,
    val description: String,
    val date: Date,
    val imageUrl: String? = null
)