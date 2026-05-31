package com.example.budget_buddie_sa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budget_buddie_sa.adapter.ExpenseAdapter
import com.example.budget_buddie_sa.viewmodel.DashboardViewModel
import java.util.*

/**
 * Dashboard screen following MVVM pattern.
 * It observes real-time data from the ViewModel and updates the UI automatically.
 */
class DashboardActivity : BaseNavigationActivity() {

    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var expenseAdapter: ExpenseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        supportActionBar?.title = "Dashboard"

        val tvTotalSpending = findViewById<TextView>(R.id.tvTotalSpending)
        val tvRemainingBudget = findViewById<TextView>(R.id.tvRemainingBudget)
        val tvCategoryCount = findViewById<TextView>(R.id.tvCategoryCount)
        val tvMonthTotal = findViewById<TextView>(R.id.tvMonthTotal)
        val tvBudgetUsedPercent = findViewById<TextView>(R.id.tvBudgetUsedPercent)
        val pbBudgetTracking = findViewById<ProgressBar>(R.id.pbBudgetTracking)
        val btnAddExpense = findViewById<Button>(R.id.btnAddExpense)
        val rvRecentExpenses = findViewById<RecyclerView>(R.id.rvRecentExpenses)
        val tvSeeAll = findViewById<TextView>(R.id.tvSeeAll)

        // Setup RecyclerView for recent transactions
        expenseAdapter = ExpenseAdapter(emptyList())
        rvRecentExpenses.layoutManager = LinearLayoutManager(this)
        rvRecentExpenses.adapter = expenseAdapter

        // Observe Total Spending
        viewModel.totalSpendingValue.observe(this) { spending ->
            val total = spending ?: 0.0
            tvTotalSpending.text = String.format(Locale.getDefault(), "R %.2f", total)
        }

        // Observe This Month Spending
        viewModel.thisMonthSpending.observe(this) { spending ->
            val total = spending ?: 0.0
            tvMonthTotal.text = String.format(Locale.getDefault(), "R %.2f", total)
        }

        // Observe Remaining Budget
        viewModel.remainingBudget.observe(this) { remaining ->
            tvRemainingBudget.text = String.format(Locale.getDefault(), "R %.2f", remaining)
        }

        // Observe Spending Progress for Progress Bar
        viewModel.spendingProgress.observe(this) { progress ->
            pbBudgetTracking.progress = progress
        }

        // Observe Spending Percentage Text
        viewModel.spendingPercentText.observe(this) { percent ->
            tvBudgetUsedPercent.text = "$percent%"
        }
        
        // Observe Category Count
        viewModel.categoryCount.observe(this) { count ->
            tvCategoryCount.text = "$count Active"
        }

        // Observe Recent Expenses
        viewModel.recentExpenses.observe(this) { expenses ->
            expenseAdapter.updateData(expenses)
        }

        btnAddExpense.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }
        
        tvSeeAll.setOnClickListener {
            startActivity(Intent(this, ExpenseListActivity::class.java))
        }
    }
}
