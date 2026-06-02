package com.example.budget_buddie_sa

import android.app.Application
import com.example.budget_buddie_sa.data.local.AppDatabase
import com.example.budget_buddie_sa.data.repository.*

class BudgetApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    
    // Core Cloud Repositories
    val firebaseSyncRepository by lazy { FirebaseSyncRepository() }
    val storageRepository by lazy { StorageRepository() }

    // Domain Repositories
    val expenseRepository by lazy { 
        ExpenseRepository(database.expenseDao(), firebaseSyncRepository, storageRepository) 
    }
    val budgetRepository by lazy { 
        BudgetRepository(database.budgetDao(), firebaseSyncRepository) 
    }
    val categoryRepository by lazy { 
        CategoryRepository(database.categoryDao(), firebaseSyncRepository, storageRepository) 
    }
    val userRepository by lazy {
        UserRepository(database.userDao(), firebaseSyncRepository)
    }
}
