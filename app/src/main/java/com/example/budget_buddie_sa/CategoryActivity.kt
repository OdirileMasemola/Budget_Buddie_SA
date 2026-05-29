package com.example.budget_buddie_sa

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budget_buddie_sa.adapter.CategoryAdapter
import com.example.budget_buddie_sa.data.model.Category
import com.example.budget_buddie_sa.viewmodel.CategoryViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CategoryActivity : BaseNavigationActivity() {

    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var adapter: CategoryAdapter
    private var tempSelectedImageUri: String? = null
    private var ivPreview: ImageView? = null

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            tempSelectedImageUri = uri.toString()
            ivPreview?.visibility = View.VISIBLE
            ivPreview?.setImageURI(uri)
            Toast.makeText(this, "Image Selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        categoryViewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)
        supportActionBar?.title = "Categories"

        val rvCategories = findViewById<RecyclerView>(R.id.rvCategories)
        val btnAddCategory = findViewById<FloatingActionButton>(R.id.btnAddCategory)

        adapter = CategoryAdapter(emptyList(), { category ->
            showEditCategoryDialog(category)
        }, { category ->
            categoryViewModel.deleteCategory(category)
            Toast.makeText(this, "Category deleted", Toast.LENGTH_SHORT).show()
        })
        
        rvCategories.layoutManager = LinearLayoutManager(this)
        rvCategories.adapter = adapter

        categoryViewModel.allCategories.observe(this) { categories ->
            adapter.updateData(categories)
        }

        btnAddCategory.setOnClickListener {
            showAddCategoryDialog()
        }
    }

    private fun showAddCategoryDialog() {
        tempSelectedImageUri = null
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null)
        val etCategoryName = dialogView.findViewById<EditText>(R.id.etCategoryName)
        val btnPickImage = dialogView.findViewById<MaterialCardView>(R.id.btnPickImage)
        val btnCancel = dialogView.findViewById<MaterialButton>(R.id.btnCancel)
        ivPreview = dialogView.findViewById(R.id.ivSelectedPreview)

        var selectedColor = "#7C3AED" // Default Purple

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = etCategoryName.text.toString().trim()
                if (name.isNotEmpty()) {
                    val category = Category(
                        userId = 0, // Will be set in ViewModel
                        name = name,
                        color = selectedColor,
                        imageUri = tempSelectedImageUri
                    )
                    categoryViewModel.insertCategory(category)
                    Toast.makeText(this, "Category added!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        val colorViews = listOf(
            dialogView.findViewById<View>(R.id.color1),
            dialogView.findViewById<View>(R.id.color2),
            dialogView.findViewById<View>(R.id.color3),
            dialogView.findViewById<View>(R.id.color4),
            dialogView.findViewById<View>(R.id.color5)
        )

        colorViews.forEach { view ->
            view.setOnClickListener {
                selectedColor = it.tag.toString()
                colorViews.forEach { v -> v.alpha = 0.5f }
                it.alpha = 1.0f
                // Clear image if color is picked? Requirement says OR but better to be clear.
                tempSelectedImageUri = null
                ivPreview?.visibility = View.GONE
            }
        }

        btnPickImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        dialog.show()
    }

    private fun showEditCategoryDialog(category: Category) {
        tempSelectedImageUri = category.imageUri
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null)
        val etCategoryName = dialogView.findViewById<EditText>(R.id.etCategoryName)
        val btnPickImage = dialogView.findViewById<MaterialCardView>(R.id.btnPickImage)
        val btnCancel = dialogView.findViewById<MaterialButton>(R.id.btnCancel)
        ivPreview = dialogView.findViewById(R.id.ivSelectedPreview)

        etCategoryName.setText(category.name)
        if (tempSelectedImageUri != null) {
            ivPreview?.visibility = View.VISIBLE
            ivPreview?.setImageURI(Uri.parse(tempSelectedImageUri))
        }

        var selectedColor = category.color

        val colorViews = listOf(
            dialogView.findViewById<View>(R.id.color1),
            dialogView.findViewById<View>(R.id.color2),
            dialogView.findViewById<View>(R.id.color3),
            dialogView.findViewById<View>(R.id.color4),
            dialogView.findViewById<View>(R.id.color5)
        )

        colorViews.forEach { view ->
            if (view.tag.toString() == selectedColor) view.alpha = 1.0f else view.alpha = 0.5f
            view.setOnClickListener {
                selectedColor = it.tag.toString()
                colorViews.forEach { v -> v.alpha = 0.5f }
                it.alpha = 1.0f
                tempSelectedImageUri = null
                ivPreview?.visibility = View.GONE
            }
        }

        btnPickImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val name = etCategoryName.text.toString().trim()
                if (name.isNotEmpty()) {
                    val updatedCategory = category.copy(
                        name = name,
                        color = selectedColor,
                        imageUri = tempSelectedImageUri
                    )
                    categoryViewModel.updateCategory(updatedCategory)
                    Toast.makeText(this, "Category updated!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
