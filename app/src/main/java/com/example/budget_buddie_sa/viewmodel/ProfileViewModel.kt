package com.example.budget_buddie_sa.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.budget_buddie_sa.BudgetApp
import com.example.budget_buddie_sa.SessionManager
import com.example.budget_buddie_sa.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for the Profile page.
 * Manages fetching user data and stats safely.
 */
class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val db = (application as BudgetApp).database
    private val userDao = db.userDao()
    private val expenseRepo = (application as BudgetApp).expenseRepository
    private val categoryRepo = (application as BudgetApp).categoryRepository
    private val sessionManager = SessionManager(application)
    private val userId = sessionManager.getUserId()

    private val _userProfile = MutableLiveData<User?>()
    val userProfile: LiveData<User?> get() = _userProfile

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    // Stats exposed as LiveData
    val totalSpent: LiveData<Double?> = expenseRepo.getTotalSpendingForUser(userId).asLiveData()
    val categoryCount: LiveData<Int> = categoryRepo.getCategoriesForUser(userId).map { it.size }.asLiveData()
    val expenseCount: LiveData<Int> = expenseRepo.getExpensesForUser(userId).map { it.size }.asLiveData()

    init {
        fetchUserProfile()
    }

    /**
     * Fetches user details by ID from the database using the session ID.
     */
    fun fetchUserProfile() {
        if (userId == -1) {
            _error.value = "Invalid Session"
            return
        }

        viewModelScope.launch {
            try {
                val user = withContext(Dispatchers.IO) {
                    userDao.getUserById(userId)
                }
                if (user != null) {
                    _userProfile.value = user
                } else {
                    _error.value = "User not found"
                }
            } catch (e: Exception) {
                _error.value = "Error fetching profile: ${e.message}"
            }
        }
    }
}
