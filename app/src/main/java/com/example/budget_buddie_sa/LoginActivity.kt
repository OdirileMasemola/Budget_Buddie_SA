package com.example.budget_buddie_sa

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.budget_buddie_sa.viewmodel.AuthViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

/**
 * Login screen supporting Email/Password and Google Sign-In via Credential Manager.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var authViewModel: AuthViewModel
    private lateinit var credentialManager: CredentialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        sessionManager = SessionManager(this)
        
        // If already logged in, skip to Dashboard
        if (sessionManager.isLoggedIn()) {
            navigateToDashboard()
            return
        }

        setContentView(R.layout.activity_login)
        credentialManager = CredentialManager.create(this)

        val imageView = findViewById<ImageView>(R.id.ivLogo)
        val options = BitmapFactory.Options().apply { inSampleSize = 4 }
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.login, options)
        imageView.setImageBitmap(bitmap)

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnGoogleSignIn = findViewById<Button>(R.id.btnGoogleSignIn)
        val tvRegisterLink = findViewById<TextView>(R.id.tvRegisterLink)

        // Observe authentication state
        authViewModel.authState.observe(this) { result ->
            when (result) {
                is AuthViewModel.AuthResult.Success -> {
                    Log.d("LoginActivity", "Auth Success: Saving session for UID ${result.user.id}")
                    sessionManager.saveSession(
                        userId = result.user.id,
                        email = result.user.email,
                        displayName = result.user.username
                    )
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    navigateToDashboard()
                }
                is AuthViewModel.AuthResult.Error -> {
                    Log.e("LoginActivity", "Auth Error: ${result.message}")
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
                else -> {} 
            }
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (!isValidEmail(email)) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty() || password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            authViewModel.login(email, password)
        }

        btnGoogleSignIn.setOnClickListener {
            // Google Sign-In is separate and does not validate email/password fields
            signInWithGoogle()
        }

        tvRegisterLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


    private fun signInWithGoogle() {
        Log.d("LoginActivity", "Google button clicked. Web Client ID: ${getString(R.string.default_web_client_id)}")
        Toast.makeText(this, "Opening Google picker...", Toast.LENGTH_SHORT).show()

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.default_web_client_id))
            .setAutoSelectEnabled(false) // Set to false to force picker for debugging
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                Log.d("LoginActivity", "Calling credentialManager.getCredential")
                val result = credentialManager.getCredential(
                    request = request,
                    context = this@LoginActivity
                )
                Log.d("LoginActivity", "getCredential returned successfully")
                handleGoogleLogin(result)
            } catch (e: GetCredentialException) {
                Log.e("LoginActivity", "Google Sign-In Failed: Type=${e.type}, Message=${e.message}", e)
                Toast.makeText(this@LoginActivity, "Google Sign-In failed: ${e.message}", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("LoginActivity", "Unexpected error during Google Sign-In", e)
                Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleGoogleLogin(result: GetCredentialResponse) {
        val credential = result.credential
        Log.d("LoginActivity", "Google credential received of type: ${credential.type}")

        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                // Parse the credential using the static method
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                
                val idToken = googleIdTokenCredential.idToken
                val displayName = googleIdTokenCredential.displayName
                val firstName = googleIdTokenCredential.givenName
                val lastName = googleIdTokenCredential.familyName
                val profilePic = googleIdTokenCredential.profilePictureUri

                Log.d("LoginActivity", "Google ID token extracted: ${idToken.take(10)}...")
                
                // Requirement 7: Pass extracted info to ViewModel
                authViewModel.loginWithGoogle(
                    idToken = idToken,
                    firstName = firstName,
                    lastName = lastName,
                    displayName = displayName
                )
            } catch (e: Exception) {
                Log.e("LoginActivity", "Error parsing Google ID token: ${e.message}", e)
                Toast.makeText(this, "Login Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Requirement 8: Removed "Unexpected credential type" error logic
            Log.d("LoginActivity", "Received non-Google ID token credential: ${credential.type}")
        }
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}
