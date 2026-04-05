package com.example.budget_buddie_sa

import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddExpenseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        val etAmount = findViewById<EditText>(R.id.etAmount)
        val datePicker = findViewById<DatePicker>(R.id.datePicker)
        val spinnerCategory = findViewById<Spinner>(R.id.spinnerCategory)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val btnUploadImage = findViewById<Button>(R.id.btnUploadImage)
        val btnSaveExpense = findViewById<Button>(R.id.btnSaveExpense)

        // Save expense button click listener
        btnSaveExpense.setOnClickListener {
            // Placeholder for save logic
            Toast.makeText(this, "Expense Saved", Toast.LENGTH_SHORT).show()
            finish() // Return to previous screen
        }

        // Upload image button click listener (no implementation needed yet)
        btnUploadImage.setOnClickListener {
            Toast.makeText(this, "Upload Image Clicked", Toast.LENGTH_SHORT).show()
        }
    }
}