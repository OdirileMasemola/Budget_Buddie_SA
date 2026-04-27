package com.example.budget_buddie_sa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.budget_buddie_sa.viewmodel.AuthViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        sessionManager = SessionManager(this)
        
        // If already logged in, skip to Dashboard
        if (sessionManager.isLoggedIn()) {
            navigateToDashboard()
            return
        }

        setContentView(R.layout.activity_login)

        // Initialize ViewModel using ViewModelProvider (Standard way)
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegisterLink = findViewById<TextView>(R.id.tvRegisterLink)

        // Observe authentication state from ViewModel
        authViewModel.authState.observe(this) { result ->
            when (result) {
                is AuthViewModel.AuthResult.Success -> {
                    // Save session and navigate
                    sessionManager.saveSession(result.user.id)
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    navigateToDashboard()
                }
                is AuthViewModel.AuthResult.Error -> {
                    // Show error message
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
                else -> {} 
            }
        }

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Call login logic in ViewModel (Off-main-thread)
            authViewModel.login(username, password)
        }

        tvRegisterLink.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}
