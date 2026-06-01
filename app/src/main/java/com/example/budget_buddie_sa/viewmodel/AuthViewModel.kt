package com.example.budget_buddie_sa.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.budget_buddie_sa.data.local.AppDatabase
import com.example.budget_buddie_sa.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * ViewModel for handling Firebase Authentication (Email/Password & Google Sign-In).
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userDao = AppDatabase.getDatabase(application).userDao()

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
                    // Sync with local Room DB
                    var localUser = withContext(Dispatchers.IO) {
                        userDao.getUserById(firebaseUser.uid)
                    }
                    if (localUser == null) {
                        localUser = User(
                            id = firebaseUser.uid,
                            email = firebaseUser.email ?: "",
                            username = firebaseUser.displayName ?: firebaseUser.email?.split("@")?.get(0) ?: "User"
                        )
                        withContext(Dispatchers.IO) {
                            userDao.insert(localUser!!)
                        }
                    }
                    _authState.value = AuthResult.Success(localUser!!)
                } else {
                    _authState.value = AuthResult.Error("Login failed: User null")
                }
            } catch (e: Exception) {
                _authState.value = AuthResult.Error("Login Error: ${e.message}")
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
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        username = email.split("@")[0]
                    )
                    withContext(Dispatchers.IO) {
                        userDao.insert(newUser)
                    }
                    _authState.value = AuthResult.RegisterSuccess
                }
            } catch (e: Exception) {
                _authState.value = AuthResult.Error("Registration Failed: ${e.message}")
            }
        }
    }

    /**
     * Handles Google Sign-In Firebase Authentication using ID Token from Credential Manager.
     */
    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val result = auth.signInWithCredential(credential).await()
                val firebaseUser = result.user
                if (firebaseUser != null) {
                    // Sync with local Room DB
                    var localUser = withContext(Dispatchers.IO) {
                        userDao.getUserById(firebaseUser.uid)
                    }
                    if (localUser == null) {
                        localUser = User(
                            id = firebaseUser.uid,
                            firstName = firebaseUser.displayName?.split(" ")?.getOrNull(0) ?: "",
                            lastName = firebaseUser.displayName?.split(" ")?.getOrNull(1) ?: "",
                            email = firebaseUser.email ?: "",
                            username = firebaseUser.displayName ?: firebaseUser.email?.split("@")?.get(0) ?: "User"
                        )
                        withContext(Dispatchers.IO) {
                            userDao.insert(localUser!!)
                        }
                    }
                    _authState.value = AuthResult.Success(localUser!!)
                }
            } catch (e: Exception) {
                _authState.value = AuthResult.Error("Google Login Failed: ${e.message}")
            }
        }
    }

    sealed class AuthResult {
        data class Success(val user: User) : AuthResult()
        object RegisterSuccess : AuthResult()
        data class Error(val message: String) : AuthResult()
    }
}
