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
 * ViewModel for the Profile page.
 * Manages fetching user data safely.
 */
class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = AppDatabase.getDatabase(application).userDao()

    private val _userProfile = MutableLiveData<User?>()
    val userProfile: LiveData<User?> get() = _userProfile

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    /**
     * Fetches user details by ID from the database.
     */
    fun fetchUserProfile(userId: Int) {
        if (userId == -1) {
            _error.value = "Invalid User ID"
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
