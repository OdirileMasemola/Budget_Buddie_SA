package com.example.budget_buddie_sa.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a user of the application.
 * Updated to use Firebase UID as String.
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String, // Firebase UID
    val email: String = "",
    val displayName: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
