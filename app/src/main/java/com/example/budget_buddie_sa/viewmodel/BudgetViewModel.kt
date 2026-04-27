package com.example.budget_buddie_sa.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.budget_buddie_sa.BudgetApp
import com.example.budget_buddie_sa.data.model.Budget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BudgetViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = (application as BudgetApp).budgetRepository
    val currentBudget: LiveData<Budget?> = repository.budget.asLiveData()

    fun saveBudget(minAmount: Double, maxAmount: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val budget = Budget(id = 1, minAmount = minAmount, maxAmount = maxAmount)
            repository.insert(budget)
        }
    }
}
