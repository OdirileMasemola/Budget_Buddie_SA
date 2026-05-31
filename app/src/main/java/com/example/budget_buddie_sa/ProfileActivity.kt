package com.example.budget_buddie_sa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.budget_buddie_sa.viewmodel.ProfileViewModel

class ProfileActivity : BaseNavigationActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sessionManager = SessionManager(this)
        
        // Ensure user is logged in
        if (!sessionManager.isLoggedIn()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Initialize ViewModel
        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        val tvFullName = findViewById<TextView>(R.id.tvFullName)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val tvUsername = findViewById<TextView>(R.id.tvUsername)
        val tvEmailDetail = findViewById<TextView>(R.id.tvEmailDetail)
        
        val tvStatExpenses = findViewById<TextView>(R.id.tvStatExpenses)
        val tvStatCategories = findViewById<TextView>(R.id.tvStatCategories)
        val tvStatSpent = findViewById<TextView>(R.id.tvStatSpent)
        
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        // Observe profile data (Requirement 4, 5, 6, 7)
        profileViewModel.userProfile.observe(this) { user ->
            user?.let {
                // If full name is not set, fallback to username (Requirement 7)
                val fullName = if (it.firstName.isBlank() && it.lastName.isBlank()) {
                    it.username
                } else {
                    "${it.firstName} ${it.lastName}".trim()
                }
                
                tvFullName.text = fullName
                tvUsername.text = it.username
                
                // Set email in both header and details (Requirement 5)
                tvEmail.text = it.email
                tvEmailDetail.text = it.email
            }
        }

        // Observe stats for a complete profile (Optional but recommended)
        profileViewModel.expenseCount.observe(this) { count ->
            tvStatExpenses.text = count.toString()
        }
        
        profileViewModel.categoryCount.observe(this) { count ->
            tvStatCategories.text = count.toString()
        }
        
        profileViewModel.totalSpent.observe(this) { total ->
            tvStatSpent.text = String.format(java.util.Locale.getDefault(), "R %.0f", total ?: 0.0)
        }

        // Observe errors
        profileViewModel.error.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        // Fetching is now handled by ViewModel init, but can be triggered manually:
        // profileViewModel.fetchUserProfile()

        btnLogout.setOnClickListener {
            // Requirement 10: Logout clears session
            sessionManager.clearSession()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
