package com.example.budget_buddie_sa.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.budget_buddie_sa.data.model.Expense
import com.example.budget_buddie_sa.repository.ExpenseRepository

class DashboardViewModel : ViewModel() {

    private val repository = ExpenseRepository()

    private val _remainingBudget = MutableLiveData<String>()
    val remainingBudget: LiveData<String> = _remainingBudget

    private val _totalSpending = MutableLiveData<String>()
    val totalSpending: LiveData<String> = _totalSpending

    private val _spendingProgress = MutableLiveData<Int>()
    val spendingProgress: LiveData<Int> = _spendingProgress

    private val _recentExpenses = MutableLiveData<List<Expense>>()
    val recentExpenses: LiveData<List<Expense>> = _recentExpenses

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        _remainingBudget.value = "R 5,250.00"
        _totalSpending.value = "R 1,420.00"
        _spendingProgress.value = 65
        _recentExpenses.value = repository.getDummyHistory()
    }
}
