package com.example.budget_buddie_sa

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budget_buddie_sa.adapter.ExpenseAdapter
import com.example.budget_buddie_sa.viewmodel.ExpenseViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ExpenseListActivity : BaseNavigationActivity() {

    private val viewModel: ExpenseViewModel by viewModels()
    private lateinit var adapter: ExpenseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_list)

        supportActionBar?.title = "Expenses"

        val rvExpenses = findViewById<RecyclerView>(R.id.rvExpenses)
        val fabAddExpense = findViewById<FloatingActionButton>(R.id.fabAddExpense)

        adapter = ExpenseAdapter(emptyList())
        rvExpenses.layoutManager = LinearLayoutManager(this)
        rvExpenses.adapter = adapter

        // Observe all expenses from the database
        viewModel.allExpenses.observe(this) { expenses ->
            adapter.updateData(expenses)
        }

        fabAddExpense.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            startActivity(intent)
        }
    }
}
