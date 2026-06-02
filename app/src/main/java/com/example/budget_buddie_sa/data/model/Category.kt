package com.example.budget_buddie_sa.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a category for expenses.
 */
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey
    val id: String, // Use String ID for Firestore sync
    val userId: String,
    val name: String,
    val color: String = "#7C3AED",
    val imageUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
