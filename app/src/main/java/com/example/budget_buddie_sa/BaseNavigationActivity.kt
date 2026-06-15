package com.example.budget_buddie_sa

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

/**
 * Base Activity that handles common navigation using a BottomNavigationView.
 * Other activities should extend this class to have the bottom navigation bar.
 */
abstract class BaseNavigationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Requirement 7: Check if user is logged in
        val sessionManager = SessionManager(this)
        if (!sessionManager.isLoggedIn()) {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun setContentView(layoutResID: Int) {
        val baseLayout = layoutInflater.inflate(R.layout.activity_base_nav, null)
        val activityContainer = baseLayout.findViewById<FrameLayout>(R.id.content_frame)

        // Inflate the actual activity layout into the container
        layoutInflater.inflate(layoutResID, activityContainer, true)
        
        super.setContentView(baseLayout)

        setupNavigation()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                startActivity(Intent(this, ProfileActivity::class.java))
                true
            }
            R.id.action_badges -> {
                startActivity(Intent(this, BadgesActivity::class.java))
                true
            }
            R.id.action_logout -> {
                val sessionManager = SessionManager(this)
                sessionManager.clearSession()
                // Also sign out from Firebase and Google
                FirebaseAuth.getInstance().signOut()
                
                val credentialManager = androidx.credentials.CredentialManager.create(this)
                lifecycleScope.launch {
                    try {
                        credentialManager.clearCredentialState(androidx.credentials.ClearCredentialStateRequest())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                true
            }
            // Add other actions (badges, reports, settings) if activities exist
            R.id.action_reports -> {
                startActivity(Intent(this, ReportsActivity::class.java))
                true
            }
            R.id.action_settings -> {
                startActivity(Intent(this, BudgetActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupNavigation() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Show back button for activities that are NOT main navigation targets
        val isMainTarget = this is DashboardActivity || 
                          this is ExpenseListActivity || this is BudgetActivity || 
                          this is CategoryActivity
        
        if (!isMainTarget) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        
        // Highlight the correct item based on the current activity
        when (this) {
            is DashboardActivity -> bottomNav.selectedItemId = R.id.nav_dashboard
            is ExpenseListActivity -> bottomNav.selectedItemId = R.id.nav_history
            is CategoryActivity -> bottomNav.selectedItemId = R.id.nav_categories
            is BudgetActivity -> bottomNav.selectedItemId = R.id.nav_budget
            else -> {
                // For sub-screens, we might want to uncheck all items
                bottomNav.menu.setGroupCheckable(0, true, false)
                for (i in 0 until bottomNav.menu.size()) {
                    bottomNav.menu.getItem(i).isChecked = false
                }
                bottomNav.menu.setGroupCheckable(0, true, true)
            }
        }

        bottomNav.setOnItemSelectedListener { item ->
            val targetActivity = when (item.itemId) {
                R.id.nav_dashboard -> DashboardActivity::class.java
                R.id.nav_history -> ExpenseListActivity::class.java
                R.id.nav_categories -> CategoryActivity::class.java
                R.id.nav_budget -> BudgetActivity::class.java
                else -> null
            }

            if (targetActivity != null && this::class.java != targetActivity) {
                startActivity(Intent(this, targetActivity))
                // Use a subtle transition to avoid flickering
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                true
            } else {
                false
            }
        }
    }
}
