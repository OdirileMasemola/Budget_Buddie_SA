package com.example.budget_buddie_sa.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.budget_buddie_sa.BudgetApp
import com.example.budget_buddie_sa.SessionManager
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class BadgeViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as BudgetApp
    private val badgeRepo = app.badgeRepository
    private val expenseRepo = app.expenseRepository
    private val budgetRepo = app.budgetRepository
    private val categoryRepo = app.categoryRepository
    private val sessionManager = SessionManager(application)
    private val userId = sessionManager.getUserId() ?: ""

    val badges = badgeRepo.getBadgesForUser(userId).asLiveData()

    init {
        if (userId.isNotEmpty()) {
            observeDataChanges()
        }
    }

    private fun observeDataChanges() {
        viewModelScope.launch {
            combine(
                expenseRepo.getExpensesForUser(userId),
                budgetRepo.getBudgetForUser(userId),
                categoryRepo.getCategoriesForUser(userId)
            ) { expenses, budget, categories ->
                Triple(expenses, budget, categories)
            }.collect { (expenses, budget, categories) ->
                badgeRepo.refreshBadges(userId, expenses, budget, categories)
            }
        }
    }
}
