package com.example.budget_buddie_sa

import android.app.Application
import com.example.budget_buddie_sa.data.local.AppDatabase
import com.example.budget_buddie_sa.data.repository.BudgetRepository
import com.example.budget_buddie_sa.data.repository.ExpenseRepository

class BudgetApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val expenseRepository by lazy { ExpenseRepository(database.expenseDao()) }
    val budgetRepository by lazy { BudgetRepository(database.budgetDao()) }
}
