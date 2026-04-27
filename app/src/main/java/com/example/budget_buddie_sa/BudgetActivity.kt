package com.example.budget_buddie_sa

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import com.example.budget_buddie_sa.viewmodel.BudgetViewModel

class BudgetActivity : BaseNavigationActivity() {

    private val viewModel: BudgetViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget)

        supportActionBar?.title = "Budget Settings"

        val etMinBudget = findViewById<EditText>(R.id.etMinBudget)
        val etMaxBudget = findViewById<EditText>(R.id.etMaxBudget)
        val btnSaveBudget = findViewById<Button>(R.id.btnSaveBudget)

        // Populate existing budget if available
        viewModel.currentBudget.observe(this) { budget ->
            budget?.let {
                etMinBudget.setText(it.minAmount.toString())
                etMaxBudget.setText(it.maxAmount.toString())
            }
        }

        btnSaveBudget.setOnClickListener {
            val minStr = etMinBudget.text.toString()
            val maxStr = etMaxBudget.text.toString()
            
            if (minStr.isNotEmpty() && maxStr.isNotEmpty()) {
                viewModel.saveBudget(minStr.toDouble(), maxStr.toDouble())
                Toast.makeText(this, "Budget saved successfully!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Please enter both amounts", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
