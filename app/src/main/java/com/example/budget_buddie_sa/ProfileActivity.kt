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
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        // Observe profile data
        profileViewModel.userProfile.observe(this) { user ->
            user?.let {
                tvFullName.text = "${it.firstName} ${it.lastName}"
                tvEmail.text = it.email
                tvUsername.text = it.username
            }
        }

        // Observe errors
        profileViewModel.error.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        // Fetch user data via ViewModel
        val userId = sessionManager.getUserId()
        profileViewModel.fetchUserProfile(userId)

        btnLogout.setOnClickListener {
            sessionManager.clearSession()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
