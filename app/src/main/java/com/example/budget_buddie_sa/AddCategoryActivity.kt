package com.example.budget_buddie_sa

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.budget_buddie_sa.viewmodel.CategoryViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

class AddCategoryActivity : BaseNavigationActivity() {

    private lateinit var categoryViewModel: CategoryViewModel
    private var tempSelectedImageUri: String? = null
    private lateinit var ivPreview: ImageView

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            Log.d("AddCategory", "Image selected: $uri")
            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
            try {
                contentResolver.takePersistableUriPermission(uri, flag)
            } catch (e: Exception) {
                Log.e("AddCategory", "Failed to take persistable permission: ${e.message}")
            }
            tempSelectedImageUri = uri.toString()
            ivPreview.visibility = View.VISIBLE
            
            Glide.with(this)
                .load(uri)
                .centerCrop()
                .override(300, 300)
                .into(ivPreview)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        categoryViewModel = ViewModelProvider(this).get(CategoryViewModel::class.java)
        supportActionBar?.title = "Add Category"

        val etCategoryName = findViewById<EditText>(R.id.etCategoryName)
        val btnPickImage = findViewById<MaterialCardView>(R.id.btnPickImage)
        val btnSaveCategory = findViewById<View>(R.id.btnSaveCategory)
        ivPreview = findViewById(R.id.ivSelectedPreview)

        var selectedColor = "#7C3AED" // Default Purple

        val colorViews = listOf(
            findViewById<View>(R.id.color1),
            findViewById<View>(R.id.color2),
            findViewById<View>(R.id.color3),
            findViewById<View>(R.id.color4),
            findViewById<View>(R.id.color5)
        )

        colorViews.forEach { view ->
            view.setOnClickListener {
                selectedColor = it.tag.toString()
                colorViews.forEach { v -> v.alpha = 0.5f }
                it.alpha = 1.0f
                tempSelectedImageUri = null
                ivPreview.visibility = View.GONE
            }
        }

        btnPickImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        btnSaveCategory.setOnClickListener {
            val name = etCategoryName.text.toString().trim()
            if (name.isNotEmpty()) {
                val imageUri = tempSelectedImageUri?.let { Uri.parse(it) }
                Log.d("AddCategory", "Initiating save for category: $name with selected image: $tempSelectedImageUri")
                categoryViewModel.insertCategory(name, selectedColor, imageUri)
                Toast.makeText(this, "Category added!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
