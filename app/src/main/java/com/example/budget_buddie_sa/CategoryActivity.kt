package com.example.budget_buddie_sa

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CategoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        val rvCategories = findViewById<RecyclerView>(R.id.rvCategories)
        val btnAddCategory = findViewById<Button>(R.id.btnAddCategory)

        // Setup RecyclerView
        rvCategories.layoutManager = LinearLayoutManager(this)

        // Click listener for adding a category
        btnAddCategory.setOnClickListener {
            Toast.makeText(this, "Add Category Clicked", Toast.LENGTH_SHORT).show()
        }
    }
}