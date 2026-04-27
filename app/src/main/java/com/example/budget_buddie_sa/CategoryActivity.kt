package com.example.budget_buddie_sa

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budget_buddie_sa.adapter.CategoryAdapter
import com.example.budget_buddie_sa.data.local.AppDatabase
import com.example.budget_buddie_sa.data.model.Category
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CategoryActivity : BaseNavigationActivity() {

    private lateinit var database: AppDatabase
    private lateinit var adapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        database = AppDatabase.getDatabase(this)

        supportActionBar?.title = "Categories"

        val rvCategories = findViewById<RecyclerView>(R.id.rvCategories)
        val btnAddCategory = findViewById<FloatingActionButton>(R.id.btnAddCategory)

        adapter = CategoryAdapter(emptyList()) { category ->
            deleteCategory(category)
        }
        rvCategories.layoutManager = LinearLayoutManager(this)
        rvCategories.adapter = adapter

        observeCategories()

        btnAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }
    }

    private fun observeCategories() {
        lifecycleScope.launch {
            database.categoryDao().getAllCategories().collectLatest { categories ->
                adapter.updateData(categories)
            }
        }
    }

    private fun deleteCategory(category: Category) {
        lifecycleScope.launch {
            database.categoryDao().delete(category)
            Toast.makeText(this@CategoryActivity, "Category deleted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAddCategoryDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null)
        val etCategoryName = dialogView.findViewById<EditText>(R.id.etCategoryName)

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = etCategoryName.text.toString().trim()
                if (name.isNotEmpty()) {
                    saveCategory(name)
                } else {
                    Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveCategory(name: String) {
        val newCategory = Category(
            name = name,
            color = "#6200EE", // Default purple
            iconName = "ic_category" // Default icon
        )

        lifecycleScope.launch {
            database.categoryDao().insert(newCategory)
            Toast.makeText(this@CategoryActivity, "Category added!", Toast.LENGTH_SHORT).show()
        }
    }
}