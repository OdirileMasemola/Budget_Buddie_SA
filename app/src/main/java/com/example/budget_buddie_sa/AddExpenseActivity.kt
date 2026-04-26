package com.example.budget_buddie_sa

import android.os.Bundle
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.budget_buddie_sa.data.model.Expense
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 * Activity for adding a new expense with Room support and image picking.
 */
class AddExpenseActivity : BaseNavigationActivity() {

    private var selectedImageUri: String? = null

    // Photo Picker Launcher
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            selectedImageUri = uri.toString()
            Toast.makeText(this, "Receipt Image Selected", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
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

        // Setup Category Spinner (Dummy data for now, should come from CategoryDao)
        val categories = arrayOf("Food", "Transport", "Rent", "Entertainment", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        // Handle Image Upload click
        btnUploadImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // Handle Save click
        btnSaveExpense.setOnClickListener {
            val amountStr = etAmount.text.toString()
            val description = etDescription.text.toString()

            if (amountStr.isNotEmpty()) {
                val amount = amountStr.toDouble()
                val calendar = Calendar.getInstance()
                calendar.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
                val date = calendar.timeInMillis

                // Simple Room save logic (In production, use ViewModel)
                saveExpenseToDb(amount, description, date)
            } else {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveExpenseToDb(amount: Double, description: String, date: Long) {
        val repository = (application as BudgetApp).repository
        
        // Use Coroutines to save in background
        CoroutineScope(Dispatchers.IO).launch {
            val expense = Expense(
                amount = amount,
                date = date,
                description = description,
                categoryId = 1, // Defaulting to 1 for now until categories are populated
                receiptImage = selectedImageUri
            )
            repository.addExpense(expense)
            
            launch(Dispatchers.Main) {
                Toast.makeText(this@AddExpenseActivity, "Expense Saved Successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
