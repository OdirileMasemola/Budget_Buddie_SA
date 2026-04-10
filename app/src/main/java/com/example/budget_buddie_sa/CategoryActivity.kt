package com.example.budget_buddie_sa

import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CategoryActivity : BaseNavigationActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        supportActionBar?.title = "Categories"

        val rvCategories = findViewById<RecyclerView>(R.id.rvCategories)
        val btnAddCategory = findViewById<FloatingActionButton>(R.id.btnAddCategory)

        rvCategories.layoutManager = LinearLayoutManager(this)

        btnAddCategory.setOnClickListener {
            Toast.makeText(this, "Add Category Clicked", Toast.LENGTH_SHORT).show()
        }
    }
}