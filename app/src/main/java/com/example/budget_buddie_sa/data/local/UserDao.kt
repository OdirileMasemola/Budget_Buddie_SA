package com.example.budget_buddie_sa.data.local

import androidx.room.*
import com.example.budget_buddie_sa.data.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the users table.
 */
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getCurrentUser(): User?
}
