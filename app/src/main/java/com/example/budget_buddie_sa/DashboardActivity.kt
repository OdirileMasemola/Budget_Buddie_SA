package com.example.budget_buddie_sa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val tvTotalSpending = findViewById<TextView>(R.id.tvTotalSpending)
        val tvRemainingBudget = findViewById<TextView>(R.id.tvRemainingBudget)
        val pbBudgetTracking = findViewById<ProgressBar>(R.id.pbBudgetTracking)
        val btnAddExpense = findViewById<Button>(R.id.btnAddExpense)
        val btnViewExpenses = findViewById<Button>(R.id.btnViewExpenses)
        val btnCategories = findViewById<Button>(R.id.btnCategories)
        val btnBudgetSettings = findViewById<Button>(R.id.btnBudgetSettings)

        // Navigate to AddExpenseActivity
        btnAddExpense.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            startActivity(intent)
        }

        // Navigate to ExpenseListActivity
        btnViewExpenses.setOnClickListener {
            val intent = Intent(this, ExpenseListActivity::class.java)
            startActivity(intent)
        }

        // Navigate to CategoryActivity
        btnCategories.setOnClickListener {
            val intent = Intent(this, CategoryActivity::class.java)
            startActivity(intent)
        }

        // Navigate to BudgetActivity
        btnBudgetSettings.setOnClickListener {
            val intent = Intent(this, BudgetActivity::class.java)
            startActivity(intent)
        }
    }
}