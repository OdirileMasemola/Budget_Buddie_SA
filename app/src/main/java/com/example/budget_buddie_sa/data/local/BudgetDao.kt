package com.example.budget_buddie_sa.data.local

import androidx.room.*
import com.example.budget_buddie_sa.data.model.Budget
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the budgets table.
 */
@Dao
interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: Budget)

    @Update
    suspend fun update(budget: Budget)

    @Delete
    suspend fun delete(budget: Budget)

    @Query("SELECT * FROM budgets LIMIT 1")
    fun getBudget(): Flow<Budget?>
}
