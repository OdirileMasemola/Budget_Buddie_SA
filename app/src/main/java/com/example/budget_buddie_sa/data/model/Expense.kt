package com.example.budget_buddie_sa.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents an individual expense, linked to a Category and a User.
 */
@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["categoryId"]), Index(value = ["userId"])]
)
data class Expense(
    @PrimaryKey
    val id: String, // Use String ID for Firestore sync
    val userId: String,
    val amount: Double,
    val date: Long,
    val description: String,
    val categoryId: String, // Changed to String
    val imageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
