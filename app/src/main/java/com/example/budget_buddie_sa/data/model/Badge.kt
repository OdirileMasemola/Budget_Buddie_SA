package com.example.budget_buddie_sa.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a user badge for gamification.
 */
@Entity(tableName = "badges")
data class Badge(
    @PrimaryKey
    val badgeId: String,
    val userId: String,
    val badgeName: String,
    val description: String,
    val rewardType: String, // BRONZE, SILVER, GOLD
    val isUnlocked: Boolean = false,
    val currentProgress: Int = 0,
    val targetProgress: Int = 1,
    val unlockedDate: Long? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)
