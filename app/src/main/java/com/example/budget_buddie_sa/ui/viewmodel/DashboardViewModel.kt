package com.example.budget_buddie_sa.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.example.budget_buddie_sa.BudgetApp
import com.example.budget_buddie_sa.data.model.Expense

/**
 * ViewModel for the Dashboard screen.
 */
class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as  BudgetApp).repository

    private val _remainingBudget = MutableLiveData<String>()
    val remainingBudget: LiveData<String> = _remainingBudget

    private val _totalSpending = MutableLiveData<String>()
    val totalSpending: LiveData<String> = _totalSpending

    private val _spendingProgress = MutableLiveData<Int>()
    val spendingProgress: LiveData<Int> = _spendingProgress

    val recentExpenses: LiveData<List<Expense>> = repository.allExpenses.asLiveData()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        // Dummy data for demonstration
        _remainingBudget.value = "R 5,250.00"
        _totalSpending.value = "R 1,420.00"
        _spendingProgress.value = 65
    }
}