package com.example.budget_buddie_sa

import android.os.Bundle
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.budget_buddie_sa.data.model.Expense
import com.example.budget_buddie_sa.viewmodel.ExpenseViewModel
import java.util.*

/**
 * Activity for adding a new expense with Room support and ViewModel.
 */
class AddExpenseActivity : BaseNavigationActivity() {

    private val viewModel: ExpenseViewModel by viewModels()
    private var selectedImageUri: String? = null

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
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
        val btnUploadImage = findViewById<Button>(R.id.btnUploadImage)
        val btnSaveExpense = findViewById<Button>(R.id.btnSaveExpense)

        // Setup Category Spinner (Should ideally fetch from database)
        val categories = arrayOf("Food", "Transport", "Rent", "Entertainment", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        btnUploadImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        btnSaveExpense.setOnClickListener {
            val amountStr = etAmount.text.toString()
            val description = etDescription.text.toString()

            if (amountStr.isNotEmpty()) {
                val amount = amountStr.toDouble()
                val calendar = Calendar.getInstance()
                calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
                val date = calendar.timeInMillis

                val expense = Expense(
                    amount = amount,
                    date = date,
                    description = description,
                    categoryId = 1, // Default for now
                    receiptImage = selectedImageUri
                )

                // Save using ViewModel
                viewModel.addExpense(expense) {
                    Toast.makeText(this, "Expense Saved Successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } else {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
