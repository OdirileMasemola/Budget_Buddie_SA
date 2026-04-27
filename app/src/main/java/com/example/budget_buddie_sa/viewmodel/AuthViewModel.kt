package com.example.budget_buddie_sa.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.budget_buddie_sa.data.local.AppDatabase
import com.example.budget_buddie_sa.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for handling Login and Registration logic.
 * This moves database operations away from the Activities.
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = AppDatabase.getDatabase(application).userDao()

    // LiveData to communicate status back to the Activity
    private val _authState = MutableLiveData<AuthResult>()
    val authState: LiveData<AuthResult> get() = _authState

    /**
     * Handles User Login.
     * Uses viewModelScope.launch to run on a background thread.
     */
    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                // withContext(Dispatchers.IO) ensures DB operations are off the main thread
                val user = withContext(Dispatchers.IO) {
                    userDao.getUserByUsername(username)
                }

                if (user != null && user.password == password) {
                    _authState.value = AuthResult.Success(user)
                } else {
                    _authState.value = AuthResult.Error("Invalid username or password")
                }
            } catch (e: Exception) {
                _authState.value = AuthResult.Error("Database Error: ${e.message}")
            }
        }
    }

    /**
     * Handles User Registration.
     */
    fun register(user: User) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    userDao.insert(user)
                }
                _authState.value = AuthResult.RegisterSuccess
            } catch (e: Exception) {
                _authState.value = AuthResult.Error("Registration Failed: ${e.message}")
            }
        }
    }

    /**
     * Sealed class to represent different authentication outcomes.
     */
    sealed class AuthResult {
        data class Success(val user: User) : AuthResult()
        object RegisterSuccess : AuthResult()
        data class Error(val message: String) : AuthResult()
    }
}
