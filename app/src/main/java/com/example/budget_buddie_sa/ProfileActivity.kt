package com.example.budget_buddie_sa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.example.budget_buddie_sa.data.local.AppDatabase
import kotlinx.coroutines.launch

class ProfileActivity : BaseNavigationActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportActionBar?.title = "Profile"

        val tvFullName = findViewById<TextView>(R.id.tvFullName)
        val tvEmail = findViewById<TextView>(R.id.tvEmail)
        val tvUsername = findViewById<TextView>(R.id.tvUsername)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        val database = AppDatabase.getDatabase(this)

        lifecycleScope.launch {
            val user = database.userDao().getCurrentUser()
            user?.let {
                tvFullName.text = "${it.firstName} ${it.lastName}"
                tvEmail.text = it.email
                tvUsername.text = it.username
            }
        }

        btnLogout.setOnClickListener {
            // In a real app, clear session/tokens here
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}