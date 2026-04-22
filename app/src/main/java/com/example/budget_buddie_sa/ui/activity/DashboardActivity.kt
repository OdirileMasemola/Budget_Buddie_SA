package com.example.budget_buddie_sa.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import com.example.budget_buddie_sa.AddExpenseActivity
import com.example.budget_buddie_sa.BaseNavigationActivity
import com.example.budget_buddie_sa.R
import com.example.budget_buddie_sa.ui.viewmodel.DashboardViewModel

/**
 * Dashboard screen following MVVM pattern.
 * Displays budget overview and recent activity.
 */
class DashboardActivity : BaseNavigationActivity() {

    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        supportActionBar?.title = getString(R.string.title_dashboard)

        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        findViewById<Button>(R.id.btnAddExpense).setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }
    }

    private fun observeViewModel() {
        val tvTotalSpending = findViewById<TextView>(R.id.tvTotalSpending)
        val tvRemainingBudget = findViewById<TextView>(R.id.tvRemainingBudget)
        val pbBudgetTracking = findViewById<ProgressBar>(R.id.pbBudgetTracking)

        viewModel.remainingBudget.observe(this) { budget ->
            tvRemainingBudget.text = budget
        }

        viewModel.totalSpending.observe(this) { spending ->
            tvTotalSpending.text = spending
        }

        viewModel.spendingProgress.observe(this) { progress ->
            pbBudgetTracking.progress = progress
        }
    }
}
