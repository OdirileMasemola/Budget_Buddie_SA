package com.example.budget_buddie_sa.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.budget_buddie_sa.data.model.Budget
import com.example.budget_buddie_sa.data.model.Category
import com.example.budget_buddie_sa.data.model.Expense
import com.example.budget_buddie_sa.data.model.User

/**
 * Main Room database class.
 * Includes all entities and provides access to DAOs.
 */
@Database(
    entities = [Category::class, Expense::class, Budget::class, User::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun budgetDao(): BudgetDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Singleton pattern to prevent multiple database instances.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "budget_buddie_db"
                )
                .fallbackToDestructiveMigration() // Simple strategy for beginners
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
