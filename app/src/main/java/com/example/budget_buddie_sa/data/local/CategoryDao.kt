package com.example.budget_buddie_sa.data.local

import androidx.room.*
import com.example.budget_buddie_sa.data.model.Category
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the categories table.
 */
@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: Category)

    @Update
    suspend fun update(category: Category)

    @Delete
    suspend fun delete(category: Category)

    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<Category>>
}
