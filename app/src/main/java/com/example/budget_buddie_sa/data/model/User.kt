package com.example.budget_buddie_sa.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a user of the application.
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val password: String // In a real app, this should be hashed
)
