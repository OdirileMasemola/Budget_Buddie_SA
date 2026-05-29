package com.example.budget_buddie_sa.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.budget_buddie_sa.BudgetApp
import com.example.budget_buddie_sa.SessionManager
import com.example.budget_buddie_sa.data.model.Budget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class BudgetViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as BudgetApp).budgetRepository
    private val sessionManager = SessionManager(application)
    private val userId = sessionManager.getUserId()

    val currentBudget: LiveData<Budget?> = repository.getBudgetForUser(userId).asLiveData()

    fun saveBudget(min: Double, max: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingBudget = repository.getBudgetForUser(userId).firstOrNull()
            val newBudget = if (existingBudget != null) {
                existingBudget.copy(minAmount = min, maxAmount = max)
            } else {
                Budget(userId = userId, minAmount = min, maxAmount = max)
            }
            repository.insertBudget(newBudget)
        }
    }
    
    // Simpler save/update
    fun setBudget(budget: Budget) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertBudget(budget.copy(userId = userId))
        }
    }
}
