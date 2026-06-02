package com.example.budget_buddie_sa

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

class RegisterActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel
    private lateinit var credentialManager: CredentialManager
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        credentialManager = CredentialManager.create(this)
        sessionManager = SessionManager(this)

        val imageView = findViewById<ImageView>(R.id.ivRegLogo)
        val options = BitmapFactory.Options().apply { inSampleSize = 4 }
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.signup, options)
        imageView.setImageBitmap(bitmap)

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        val etFirstName = findViewById<EditText>(R.id.etFirstName)
        val etLastName = findViewById<EditText>(R.id.etLastName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etRegPassword = findViewById<EditText>(R.id.etRegPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnGoogleSignUp = findViewById<Button>(R.id.btnGoogleSignUp)
        val tvLoginLink = findViewById<TextView>(R.id.tvLoginLink)

        authViewModel.authState.observe(this) { result ->
            when (result) {
                is AuthViewModel.AuthResult.Success -> {
                    Log.d("RegisterActivity", "Auth Success: Saving session for UID ${result.user.id}")
                    sessionManager.saveSession(
                        userId = result.user.id,
                        email = result.user.email,
                        displayName = result.user.username
                    )
                    Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
                    navigateToDashboard()
                }
                AuthViewModel.AuthResult.RegisterSuccess -> {
                    Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                    finish() 
                }
                is AuthViewModel.AuthResult.Error -> {
                    Log.e("RegisterActivity", "Auth Error: ${result.message}")
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        tvLoginLink.setOnClickListener {
            finish()
        }

        btnGoogleSignUp.setOnClickListener {
            // Google Sign-In is separate and does not validate email/password fields
            signUpWithGoogle()
        }

        btnRegister.setOnClickListener {
            val firstName = etFirstName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etRegPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (firstName.isEmpty() || lastName.isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidEmail(email)) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authViewModel.register(firstName, lastName, email, password)
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }


    private fun signUpWithGoogle() {
        Log.d("RegisterActivity", "Google button clicked. Web Client ID: ${getString(R.string.default_web_client_id)}")
        Toast.makeText(this, "Opening Google picker...", Toast.LENGTH_SHORT).show()

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.default_web_client_id))
            .setAutoSelectEnabled(false)
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                Log.d("RegisterActivity", "Calling credentialManager.getCredential")
                val result = credentialManager.getCredential(
                    request = request,
                    context = this@RegisterActivity
                )
                Log.d("RegisterActivity", "getCredential returned successfully")
                handleGoogleSignUp(result)
            } catch (e: GetCredentialException) {
                Log.e("RegisterActivity", "Google Sign-Up Error: Type=${e.type}, Message=${e.message}", e)
                Toast.makeText(this@RegisterActivity, "Google Sign-Up failed: ${e.message}", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("RegisterActivity", "Unexpected error during Google Sign-Up", e)
                Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleGoogleSignUp(result: GetCredentialResponse) {
        val credential = result.credential
        Log.d("RegisterActivity", "Google credential received of type: ${credential.type}")

        if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                // Parse the credential
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                
                val idToken = googleIdTokenCredential.idToken
                val displayName = googleIdTokenCredential.displayName
                val firstName = googleIdTokenCredential.givenName
                val lastName = googleIdTokenCredential.familyName
                
                Log.d("RegisterActivity", "Google ID token extracted: ${idToken.take(10)}...")
                
                // Requirement 7: Use extracted info to create profile
                authViewModel.loginWithGoogle(
                    idToken = idToken,
                    firstName = firstName,
                    lastName = lastName,
                    displayName = displayName
                )
            } catch (e: Exception) {
                Log.e("RegisterActivity", "Error parsing Google ID token: ${e.message}", e)
                Toast.makeText(this, "Sign up Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Requirement 8: Removed "Unexpected credential type" error logic
            Log.d("RegisterActivity", "Received non-Google ID token credential: ${credential.type}")
        }
    }

    private fun navigateToDashboard() {
        val intent = android.content.Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}
