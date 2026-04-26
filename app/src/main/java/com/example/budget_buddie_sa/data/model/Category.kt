package com.example.budget_buddie_sa.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a spending category (e.g., Food, Transport).
 * Uses a predefined icon resource name.
 */
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val color: String, // Hex string
    val iconName: String // Name of the drawable icon (e.g., "ic_food")
)
