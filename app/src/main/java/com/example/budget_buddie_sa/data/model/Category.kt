package com.example.budget_buddie_sa.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a category for expenses.
 */
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String, // Updated to Firebase UID String
    val name: String,
    val color: String = "#7C3AED",
    val imageUri: String? = null
)
