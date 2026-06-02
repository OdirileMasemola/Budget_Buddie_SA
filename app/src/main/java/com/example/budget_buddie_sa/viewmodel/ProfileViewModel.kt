package com.example.budget_buddie_sa.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.budget_buddie_sa.BudgetApp
import com.example.budget_buddie_sa.SessionManager
import com.example.budget_buddie_sa.data.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for the Profile page.
 * Updated to use Firebase and String userId.
 */
class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val db = (application as BudgetApp).database
    private val userDao = db.userDao()
    private val expenseRepo = (application as BudgetApp).expenseRepository
    private val categoryRepo = (application as BudgetApp).categoryRepository
    private val sessionManager = SessionManager(application)
    private val firebaseAuth = FirebaseAuth.getInstance()
    
    private val userId: String? = sessionManager.getUserId()

    private val _userProfile = MutableLiveData<User?>()
    val userProfile: LiveData<User?> get() = _userProfile

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    // Stats exposed as LiveData
    val totalSpent: LiveData<Double?> = if (userId != null) {
        expenseRepo.getTotalSpendingForUser(userId).asLiveData()
    } else {
        MutableLiveData(0.0)
    }
    
    val categoryCount: LiveData<Int> = if (userId != null) {
        categoryRepo.getCategoriesForUser(userId).map { it.size }.asLiveData()
    } else {
        MutableLiveData(0)
    }
    
    val expenseCount: LiveData<Int> = if (userId != null) {
        expenseRepo.getExpensesForUser(userId).map { it.size }.asLiveData()
    } else {
        MutableLiveData(0)
    }

    init {
        fetchUserProfile()
    }

    /**
     * Fetches user details. Priority: Firebase Auth -> Local Room DB.
     */
    fun fetchUserProfile() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            // First show what we have from Firebase
            _userProfile.value = User(
                id = currentUser.uid,
                email = currentUser.email ?: "",
                username = currentUser.displayName ?: currentUser.email?.split("@")?.get(0) ?: "User",
                firstName = currentUser.displayName?.split(" ")?.getOrNull(0) ?: "",
                lastName = currentUser.displayName?.split(" ")?.getOrNull(1) ?: ""
            )
            
            // Then try to fetch more details from Room if needed
            viewModelScope.launch {
                try {
                    val localUser = withContext(Dispatchers.IO) {
                        userDao.getUserById(currentUser.uid)
                    }
                    if (localUser != null) {
                        _userProfile.value = localUser
                    }
                } catch (e: Exception) {
                    _error.value = "Error fetching local profile: ${e.message}"
                }
            }
        } else if (userId != null) {
             viewModelScope.launch {
                try {
                    val user = withContext(Dispatchers.IO) {
                        userDao.getUserById(userId)
                    }
                    if (user != null) {
                        _userProfile.value = user
                    }
                } catch (e: Exception) {
                    _error.value = "Error fetching profile: ${e.message}"
                }
            }
        } else {
            _error.value = "Not logged in"
        }
    }
    
    fun logout(credentialManager: androidx.credentials.CredentialManager? = null) {
        firebaseAuth.signOut()
        sessionManager.clearSession()
        
        // Requirement 1: Logout from Google Credential Manager state
        if (credentialManager != null) {
            viewModelScope.launch {
                try {
                    credentialManager.clearCredentialState(androidx.credentials.ClearCredentialStateRequest())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
