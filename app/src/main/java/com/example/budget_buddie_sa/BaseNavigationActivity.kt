package com.example.budget_buddie_sa

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Base Activity that handles common navigation using a BottomNavigationView.
 * Other activities should extend this class to have the bottom navigation bar.
 */
abstract class BaseNavigationActivity : AppCompatActivity() {

    override fun setContentView(layoutResID: Int) {
        val baseLayout = layoutInflater.inflate(R.layout.activity_base_nav, null)
        val activityContainer = baseLayout.findViewById<FrameLayout>(R.id.content_frame)

        // Inflate the actual activity layout into the container
        layoutInflater.inflate(layoutResID, activityContainer, true)
        
        super.setContentView(baseLayout)

        setupNavigation()
    }

    private fun setupNavigation() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Show back button for activities that are NOT main navigation targets
        val isMainTarget = this is DashboardActivity || this is AddExpenseActivity || 
                          this is ExpenseListActivity || this is BudgetActivity || 
                          this is ProfileActivity
        
        if (!isMainTarget) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            toolbar.setNavigationOnClickListener { onBackPressed() }
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        
        // Highlight the correct item based on the current activity
        when (this) {
            is DashboardActivity -> bottomNav.selectedItemId = R.id.nav_dashboard
            is AddExpenseActivity -> bottomNav.selectedItemId = R.id.nav_add_expense
            is ExpenseListActivity -> bottomNav.selectedItemId = R.id.nav_history
            is BudgetActivity -> bottomNav.selectedItemId = R.id.nav_budget
            is ProfileActivity -> bottomNav.selectedItemId = R.id.nav_profile
            else -> {
                // For sub-screens like CategoryActivity, we might want to uncheck all items
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
                R.id.nav_add_expense -> AddExpenseActivity::class.java
                R.id.nav_history -> ExpenseListActivity::class.java
                R.id.nav_budget -> BudgetActivity::class.java
                R.id.nav_profile -> ProfileActivity::class.java
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
