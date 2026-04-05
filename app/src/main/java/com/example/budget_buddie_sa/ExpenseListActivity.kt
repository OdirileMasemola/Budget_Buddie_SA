package com.example.budget_buddie_sa

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ExpenseListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_list)

        val rvExpenses = findViewById<RecyclerView>(R.id.rvExpenses)
        val fabAddExpense = findViewById<FloatingActionButton>(R.id.fabAddExpense)

        // Setup RecyclerView with layout manager
        rvExpenses.layoutManager = LinearLayoutManager(this)
        
        // Note: Adapter and data model would be implemented here in a real app

        // Navigate to AddExpenseActivity
        fabAddExpense.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            startActivity(intent)
        }
    }
}