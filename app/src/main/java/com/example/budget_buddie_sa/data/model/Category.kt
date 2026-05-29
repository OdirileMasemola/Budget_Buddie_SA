package com.example.budget_buddie_sa.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a spending category (e.g., Food, Transport).
 * Supports both color and image-based identification.
 * Linked to a specific user.
 */
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int, // Link to User
    val name: String,
    val color: String, // Hex string
    val imageUri: String? = null, // URI path for selected image from gallery
    val iconName: String? = null // For default icons
)
