package com.example.budget_buddie_sa

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budget_buddie_sa.adapter.CategoryAdapter
import com.example.budget_buddie_sa.viewmodel.CategoryViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CategoryActivity : BaseNavigationActivity() {

    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var adapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        // Initialize ViewModel
        categoryViewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)

        supportActionBar?.title = "Categories"

        val rvCategories = findViewById<RecyclerView>(R.id.rvCategories)
        val btnAddCategory = findViewById<FloatingActionButton>(R.id.btnAddCategory)

        adapter = CategoryAdapter(emptyList()) { category ->
            categoryViewModel.deleteCategory(category)
            Toast.makeText(this, "Category deleted", Toast.LENGTH_SHORT).show()
        }
        rvCategories.layoutManager = LinearLayoutManager(this)
        rvCategories.adapter = adapter

        // Observe categories using the ViewModel's Flow
        lifecycleScope.launch {
            categoryViewModel.allCategories.collectLatest { categories ->
                adapter.updateData(categories)
            }
        }

        btnAddCategory.setOnClickListener {
            showAddCategoryDialog()
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
                    categoryViewModel.insertCategory(name)
                    Toast.makeText(this, "Category added!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
