package com.example.budget_buddie_sa

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ExpenseListActivity : BaseNavigationActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_list)

        supportActionBar?.title = "Expenses"

        val rvExpenses = findViewById<RecyclerView>(R.id.rvExpenses)
        val fabAddExpense = findViewById<FloatingActionButton>(R.id.fabAddExpense)

        rvExpenses.layoutManager = LinearLayoutManager(this)

        fabAddExpense.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            startActivity(intent)
        }
    }
}