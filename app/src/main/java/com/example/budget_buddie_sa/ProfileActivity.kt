package com.example.budget_buddie_sa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.budget_buddie_sa.data.local.AppDatabase
import kotlinx.coroutines.launch

class ProfileActivity : BaseNavigationActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sessionManager = SessionManager(this)
        
        // Safety check: if not logged in, go to login
        if (!sessionManager.isLoggedIn()) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        supportActionBar?.title = "Profile"

        val tvFullName = findViewById<TextView>(R.id.tvFullName)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val tvUsername = findViewById<TextView>(R.id.tvUsername)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        val database = AppDatabase.getDatabase(this)
        val userId = sessionManager.getUserId()

        lifecycleScope.launch {
            val user = database.userDao().getUserById(userId)
            if (user != null) {
                tvFullName.text = "${user.firstName} ${user.lastName}"
                tvEmail.text = user.email
                tvUsername.text = user.username
            } else {
                Toast.makeText(this@ProfileActivity, "User not found", Toast.LENGTH_SHORT).show()
            }
        }

        btnLogout.setOnClickListener {
            sessionManager.clearSession()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}