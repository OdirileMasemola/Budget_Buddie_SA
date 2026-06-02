package com.example.budget_buddie_sa.viewmodel

import android.app.Application
import android.util.Log
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
                    // Requirement 2: Success navigation is handled by Activity observing Success
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
                    _authState.value = AuthResult.Error("Login failed: User not found")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Login Error: ${e.message}", e)
                val errorMessage = when (e) {
                    is com.google.firebase.auth.FirebaseAuthInvalidUserException -> "No account found with this email."
                    is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "Incorrect email or password."
                    is com.google.firebase.FirebaseException -> "Firebase Error: ${e.localizedMessage}"
                    else -> "Login Error: ${e.localizedMessage}"
                }
                _authState.value = AuthResult.Error(errorMessage)
            }
        }
    }

    /**
     * Register with Email and Password.
     */
    fun register(firstName: String, lastName: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                Log.d("AuthViewModel", "Attempting Firebase registration for $email")
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = result.user
                if (firebaseUser != null) {
                    val newUser = User(
                        id = firebaseUser.uid,
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        username = "$firstName $lastName"
                    )
                    withContext(Dispatchers.IO) {
                        userDao.insert(newUser)
                    }
                    Log.d("AuthViewModel", "Registration successful for UID ${firebaseUser.uid}")
                    _authState.value = AuthResult.Success(newUser)
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Registration Error: ${e.message}", e)
                val errorMessage = when (e) {
                    is com.google.firebase.auth.FirebaseAuthUserCollisionException -> "This email is already registered."
                    is com.google.firebase.auth.FirebaseAuthWeakPasswordException -> "Password is too weak."
                    else -> "Registration Failed: ${e.localizedMessage}"
                }
                _authState.value = AuthResult.Error(errorMessage)
            }
        }
    }

    /**
     * Handles Google Sign-In Firebase Authentication using ID Token and profile info.
     */
    fun loginWithGoogle(
        idToken: String,
        firstName: String? = null,
        lastName: String? = null,
        displayName: String? = null
    ) {
        viewModelScope.launch {
            try {
                Log.d("AuthViewModel", "Firebase credential created from ID token")
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                
                Log.d("AuthViewModel", "Calling Firebase signInWithCredential")
                val result = auth.signInWithCredential(credential).await()
                val firebaseUser = result.user
                
                if (firebaseUser != null) {
                    Log.d("AuthViewModel", "Firebase sign-in success: ${firebaseUser.uid}")
                    
                    // Requirement 7: If Google user is new, create Room profile. If exists, load it.
                    var localUser = withContext(Dispatchers.IO) {
                        userDao.getUserById(firebaseUser.uid)
                    }
                    
                    if (localUser == null) {
                        Log.d("AuthViewModel", "Creating new local user profile for Google user")
                        localUser = User(
                            id = firebaseUser.uid,
                            firstName = firstName ?: firebaseUser.displayName?.split(" ")?.getOrNull(0) ?: "",
                            lastName = lastName ?: firebaseUser.displayName?.split(" ")?.getOrNull(1) ?: "",
                            email = firebaseUser.email ?: "",
                            username = displayName ?: firebaseUser.displayName ?: firebaseUser.email?.split("@")?.get(0) ?: "GoogleUser"
                        )
                        withContext(Dispatchers.IO) {
                            userDao.insert(localUser!!)
                        }
                    } else {
                        Log.d("AuthViewModel", "Updating existing local user profile")
                        // User exists, update email/name if changed
                        val updatedUser = localUser.copy(
                            email = firebaseUser.email ?: localUser.email,
                            username = displayName ?: firebaseUser.displayName ?: localUser.username,
                            firstName = firstName ?: localUser.firstName,
                            lastName = lastName ?: localUser.lastName
                        )
                        withContext(Dispatchers.IO) {
                            userDao.update(updatedUser)
                        }
                        localUser = updatedUser
                    }
                    _authState.value = AuthResult.Success(localUser!!)
                } else {
                    Log.e("AuthViewModel", "Firebase sign-in failure: User is null")
                    _authState.value = AuthResult.Error("Firebase sign-in failed: User is null")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Firebase sign-in failure: ${e.message}", e)
                val errorMessage = when (e) {
                    is com.google.firebase.auth.FirebaseAuthInvalidUserException -> "Account disabled or not found."
                    is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "Invalid Google credentials."
                    is com.google.android.gms.common.api.ApiException -> "Google API Error (Code: ${e.statusCode}): ${e.message}"
                    else -> "Google Login Failed: ${e.localizedMessage}"
                }
                _authState.value = AuthResult.Error(errorMessage)
            }
        }
    }

    sealed class AuthResult {
        data class Success(val user: User) : AuthResult()
        object RegisterSuccess : AuthResult()
        data class Error(val message: String) : AuthResult()
    }
}
