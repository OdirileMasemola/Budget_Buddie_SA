package com.example.budget_buddie_sa

import android.os.Bundle
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.budget_buddie_sa.data.model.Category
import com.example.budget_buddie_sa.data.model.Expense
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
    private lateinit var sessionManager: SessionManager

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            selectedImageUri = uri.toString()
            Toast.makeText(this, "Receipt Image Selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        sessionManager = SessionManager(this)
        val userId = sessionManager.getUserId()

        supportActionBar?.title = "Add Expense"

        val etAmount = findViewById<EditText>(R.id.etAmount)
        val datePicker = findViewById<DatePicker>(R.id.datePicker)
        val spinnerCategory = findViewById<Spinner>(R.id.spinnerCategory)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val btnUploadImage = findViewById<MaterialCardView>(R.id.btnUploadImage)
        val btnSaveExpense = findViewById<MaterialButton>(R.id.btnSaveExpense)

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

            val expense = Expense(
                userId = userId,
                amount = amount,
                date = date,
                description = description,
                categoryId = selectedCategory.id,
                receiptImage = selectedImageUri
            )

            // Save using ViewModel
            viewModel.addExpense(expense) {
                Toast.makeText(this, "Expense Saved Successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
