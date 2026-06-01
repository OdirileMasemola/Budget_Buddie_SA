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
                    sessionManager.saveSession(result.user.id)
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    navigateToDashboard()
                }
                AuthViewModel.AuthResult.RegisterSuccess -> {
                    Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                    finish() 
                }
                is AuthViewModel.AuthResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        tvLoginLink.setOnClickListener {
            finish()
        }

        btnGoogleSignUp.setOnClickListener {
            signUpWithGoogle()
        }

        btnRegister.setOnClickListener {
            val firstName = etFirstName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etRegPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
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

    private fun signUpWithGoogle() {
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.default_web_client_id))
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = this@RegisterActivity
                )
                handleGoogleSignUp(result)
            } catch (e: GetCredentialException) {
                Log.e("RegisterActivity", "Google Sign-Up Error: ${e.message}")
                Toast.makeText(this@RegisterActivity, "Google Sign-Up failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleGoogleSignUp(result: GetCredentialResponse) {
        val credential = result.credential
        if (credential is GoogleIdTokenCredential) {
            val idToken = credential.idToken
            authViewModel.loginWithGoogle(idToken)
        } else {
            Log.e("RegisterActivity", "Unexpected credential type")
        }
    }

    private fun navigateToDashboard() {
        val intent = android.content.Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}
