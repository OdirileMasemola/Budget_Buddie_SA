package com.example.budget_buddie_sa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView

class DashboardActivity : BaseNavigationActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate dashboard content into the base navigation frame
        setContentView(R.layout.activity_dashboard)

        // Set Toolbar Title
        supportActionBar?.title = "Dashboard"

        val tvTotalSpending = findViewById<TextView>(R.id.tvTotalSpending)
        val tvRemainingBudget = findViewById<TextView>(R.id.tvRemainingBudget)
        val pbBudgetTracking = findViewById<ProgressBar>(R.id.pbBudgetTracking)
        val btnAddExpense = findViewById<Button>(R.id.btnAddExpense)

        btnAddExpense.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            startActivity(intent)
        }
    }
}