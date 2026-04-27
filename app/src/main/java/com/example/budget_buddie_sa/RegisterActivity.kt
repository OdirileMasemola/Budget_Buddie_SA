package com.example.budget_buddie_sa

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.budget_buddie_sa.data.model.User
import com.example.budget_buddie_sa.viewmodel.AuthViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize ViewModel
        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        val etFirstName = findViewById<EditText>(R.id.etFirstName)
        val etLastName = findViewById<EditText>(R.id.etLastName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etRegUsername = findViewById<EditText>(R.id.etRegUsername)
        val etRegPassword = findViewById<EditText>(R.id.etRegPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvLoginLink = findViewById<TextView>(R.id.tvLoginLink)

        // Observe registration result
        authViewModel.authState.observe(this) { result ->
            when (result) {
                AuthViewModel.AuthResult.RegisterSuccess -> {
                    Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                    finish() // Go back to login
                }
                is AuthViewModel.AuthResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }

        tvLoginLink.setOnClickListener {
            finish() // Simply finish to go back to LoginActivity
        }

        btnRegister.setOnClickListener {
            val firstName = etFirstName.text.toString().trim()
            val lastName = etLastName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val username = etRegUsername.text.toString().trim()
            val password = etRegPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            // Validate inputs
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create user object
            val newUser = User(
                firstName = firstName,
                lastName = lastName,
                email = email,
                username = username,
                password = password
            )

            // Register through ViewModel
            authViewModel.register(newUser)
        }
    }
}
