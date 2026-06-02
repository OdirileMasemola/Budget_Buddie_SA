package com.example.budget_buddie_sa

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.budget_buddie_sa.data.model.Category
import com.example.budget_buddie_sa.viewmodel.ExpenseViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import java.util.*

/**
 * Activity for adding a new expense with Room support and ViewModel.
 */
class AddExpenseActivity : BaseNavigationActivity() {

    private val viewModel: ExpenseViewModel by viewModels()
    private var selectedImageUri: String? = null
    private var categoryList: List<Category> = emptyList()

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
            try {
                contentResolver.takePersistableUriPermission(uri, flag)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            selectedImageUri = uri.toString()
            Toast.makeText(this, "Receipt Image Selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        supportActionBar?.title = "Add Expense"

        val etAmount = findViewById<EditText>(R.id.etAmount)
        val datePicker = findViewById<DatePicker>(R.id.datePicker)
        val spinnerCategory = findViewById<Spinner>(R.id.spinnerCategory)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val btnUploadImage = findViewById<MaterialCardView>(R.id.btnUploadImage)
        val btnSaveExpense = findViewById<MaterialButton>(R.id.btnSaveExpense)
        val tvManageCategories = findViewById<TextView>(R.id.tvManageCategories)

        // Observe Categories from Database
        viewModel.allCategories.observe(this) { categories ->
            categoryList = categories
            val categoryNames = categories.map { it.name }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategory.adapter = adapter
            
            if (categories.isEmpty()) {
                Toast.makeText(this, "Please add categories first!", Toast.LENGTH_LONG).show()
            }
        }

        btnUploadImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        tvManageCategories.setOnClickListener {
            startActivity(Intent(this, CategoryActivity::class.java))
        }

        btnSaveExpense.setOnClickListener {
            val amountStr = etAmount.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val selectedCategoryIndex = spinnerCategory.selectedItemPosition

            // Validation
            if (amountStr.isEmpty()) {
                etAmount.error = "Amount is required"
                return@setOnClickListener
            }
            
            val amount = amountStr.toDoubleOrNull() ?: 0.0
            if (amount <= 0) {
                etAmount.error = "Amount must be greater than zero"
                return@setOnClickListener
            }

            if (description.isEmpty()) {
                etDescription.error = "Description is required"
                return@setOnClickListener
            }

            if (selectedCategoryIndex == -1 || categoryList.isEmpty()) {
                Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val calendar = Calendar.getInstance()
            calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
            val date = calendar.timeInMillis
            
            val selectedCategory = categoryList[selectedCategoryIndex]

            // Save using ViewModel
            viewModel.addExpense(
                amount = amount,
                date = date,
                description = description,
                categoryId = selectedCategory.id,
                receiptUri = selectedImageUri?.let { Uri.parse(it) }
            ) {
                Toast.makeText(this, "Expense Saved Successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
