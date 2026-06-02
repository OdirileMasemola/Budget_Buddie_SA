package com.example.budget_buddie_sa.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.budget_buddie_sa.BudgetApp
import com.example.budget_buddie_sa.data.local.AppDatabase
import com.example.budget_buddie_sa.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * ViewModel for handling Firebase Authentication and Cloud Sync on login.
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userDao = AppDatabase.getDatabase(application).userDao()
    
    private val app = application as BudgetApp
    private val syncRepo = app.firebaseSyncRepository
    private val categoryRepo = app.categoryRepository
    private val expenseRepo = app.expenseRepository
    private val budgetRepo = app.budgetRepository
    private val userRepo = app.userRepository

    private val _authState = MutableLiveData<AuthResult>()
    val authState: LiveData<AuthResult> get() = _authState

    /**
     * Login with Email and Password.
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val firebaseUser = result.user
                if (firebaseUser != null) {
                    val localUser = handleUserAndSync(firebaseUser.uid, firebaseUser.email, firebaseUser.displayName)
                    _authState.value = AuthResult.Success(localUser)
                } else {
                    _authState.value = AuthResult.Error("Login failed: User not found")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Login Error: ${e.message}", e)
                _authState.value = AuthResult.Error(e.localizedMessage ?: "Login Error")
            }
        }
    }

    /**
     * Register with Email and Password.
     */
    fun register(firstName: String, lastName: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = result.user
                if (firebaseUser != null) {
                    val newUser = User(
                        id = firebaseUser.uid,
                        email = email,
                        displayName = "$firstName $lastName",
                        firstName = firstName,
                        lastName = lastName,
                        username = "$firstName $lastName"
                    )
                    withContext(Dispatchers.IO) {
                        userRepo.insertUser(newUser)
                    }
                    _authState.value = AuthResult.Success(newUser)
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Registration Error: ${e.message}", e)
                _authState.value = AuthResult.Error(e.localizedMessage ?: "Registration Failed")
            }
        }
    }

    /**
     * Handles Google Sign-In.
     */
    fun loginWithGoogle(idToken: String, firstName: String?, lastName: String?, displayName: String?) {
        viewModelScope.launch {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = auth.signInWithCredential(credential).await()
                val firebaseUser = result.user
                
                if (firebaseUser != null) {
                    val localUser = handleUserAndSync(firebaseUser.uid, firebaseUser.email, displayName ?: firebaseUser.displayName)
                    _authState.value = AuthResult.Success(localUser)
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Google Sign-in failure: ${e.message}", e)
                _authState.value = AuthResult.Error(e.localizedMessage ?: "Google Login Failed")
            }
        }
    }

    /**
     * Requirement 8: On login, fetch Firestore data and update RoomDB.
     */
    private suspend fun handleUserAndSync(uid: String, email: String?, displayName: String?): User {
        return withContext(Dispatchers.IO) {
            var localUser = userRepo.getUserById(uid)
            if (localUser == null) {
                localUser = User(
                    id = uid,
                    email = email ?: "",
                    displayName = displayName ?: "",
                    username = displayName ?: email?.split("@")?.get(0) ?: "User"
                )
                userRepo.insertUser(localUser)
            }
            
            // Sync data from Cloud to Room
            try {
                val cloudData = syncRepo.fetchAllUserData(uid)
                cloudData.user?.let { userRepo.syncFromCloud(it) }
                categoryRepo.syncFromCloud(cloudData.categories)
                expenseRepo.syncFromCloud(cloudData.expenses)
                budgetRepo.syncFromCloud(cloudData.budgets)
                Log.d("AuthViewModel", "Cloud sync completed for user $uid")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Cloud sync failed: ${e.message}")
            }
            
            // Re-fetch to ensure we have the latest synced user data
            userRepo.getUserById(uid) ?: localUser
        }
    }

    sealed class AuthResult {
        data class Success(val user: User) : AuthResult()
        object RegisterSuccess : AuthResult()
        data class Error(val message: String) : AuthResult()
    }
}
