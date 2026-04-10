package com.example.budget_buddie_sa

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class BudgetActivity : BaseNavigationActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget)

        supportActionBar?.title = "Budget Settings"

        val etMinBudget = findViewById<EditText>(R.id.etMinBudget)
        val etMaxBudget = findViewById<EditText>(R.id.etMaxBudget)
        val btnSaveBudget = findViewById<Button>(R.id.btnSaveBudget)

        btnSaveBudget.setOnClickListener {
            val minBudget = etMinBudget.text.toString()
            val maxBudget = etMaxBudget.text.toString()
            
            Toast.makeText(this, "Budget saved: Min $minBudget, Max $maxBudget", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}