package com.example.budget_buddie_sa.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a budget limit set by the user.
 */
@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey
    val id: String, // Use String ID for Firestore sync
    val userId: String,
    val minAmount: Double,
    val maxAmount: Double,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
