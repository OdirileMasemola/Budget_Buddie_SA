package com.example.budget_buddie_sa

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import com.example.budget_buddie_sa.viewmodel.BudgetViewModel
import java.util.*

class BudgetActivity : BaseNavigationActivity() {

    private val viewModel: BudgetViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget)

        supportActionBar?.title = "Budget Settings"

        val etMinBudget = findViewById<EditText>(R.id.etMinBudget)
        val etMaxBudget = findViewById<EditText>(R.id.etMaxBudget)
        val btnSaveBudget = findViewById<Button>(R.id.btnSaveBudget)
        val tvCurrentRange = findViewById<TextView>(R.id.tvCurrentBudgetRange)
        val pbBudgetRange = findViewById<ProgressBar>(R.id.pbBudgetRange)

        // Observe current budget and update UI
        viewModel.currentBudget.observe(this) { budget ->
            if (budget != null) {
                // Pre-fill input fields if they are empty (optional, but good for UX)
                if (etMinBudget.text.isEmpty()) etMinBudget.setText(String.format("%.0f", budget.minAmount))
                if (etMaxBudget.text.isEmpty()) etMaxBudget.setText(String.format("%.0f", budget.maxAmount))
                
                tvCurrentRange.text = String.format(Locale.getDefault(), "R %,.0f — R %,.0f", budget.minAmount, budget.maxAmount)
            } else {
                tvCurrentRange.text = "R 0 — R 0"
            }
        }

        // Observe spending progress and update ProgressBar (Requirement 4, 5, 6, 7)
        viewModel.spendingProgress.observe(this) { progress ->
            pbBudgetRange.progress = progress
        }

        btnSaveBudget.setOnClickListener {
            val minStr = etMinBudget.text.toString()
            val maxStr = etMaxBudget.text.toString()
            
            if (minStr.isEmpty() || maxStr.isEmpty()) {
                Toast.makeText(this, "Please enter both amounts", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val min = minStr.toDoubleOrNull() ?: 0.0
            val max = maxStr.toDoubleOrNull() ?: 0.0

            // Validation logic (Requirement 3)
            if (min < 0) {
                Toast.makeText(this, "Minimum budget cannot be negative", Toast.LENGTH_SHORT).show()
            } else if (max <= 0) {
                Toast.makeText(this, "Maximum budget must be greater than zero", Toast.LENGTH_SHORT).show()
            } else if (max <= min) {
                Toast.makeText(this, "Maximum budget must be greater than minimum budget", Toast.LENGTH_SHORT).show()
            } else {
                // Save to RoomDB via ViewModel (Requirement 3)
                viewModel.saveBudget(min, max)
                Toast.makeText(this, "Budget updated successfully!", Toast.LENGTH_SHORT).show()
                // Dashboard will update automatically due to Flow/LiveData
            }
        }
    }
}
