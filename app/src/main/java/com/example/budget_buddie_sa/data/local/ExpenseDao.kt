package com.example.budget_buddie_sa.data.local

import androidx.room.*
import com.example.budget_buddie_sa.data.model.Expense
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses WHERE userId = :userId ORDER BY date DESC")
    fun getExpensesForUser(userId: Int): Flow<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId")
    fun getTotalSpendingForUser(userId: Int): Flow<Double?>

    @Query("SELECT SUM(amount) FROM expenses WHERE userId = :userId AND date >= :startDate AND date <= :endDate")
    fun getSpendingForPeriod(userId: Int, startDate: Long, endDate: Long): Flow<Double?>
}
