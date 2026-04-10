package com.example.budget_buddie_sa

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast

class AddExpenseActivity : BaseNavigationActivity() {

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

        val categories = arrayOf("Food", "Transport", "Rent", "Entertainment", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        btnSaveExpense.setOnClickListener {
            val amount = etAmount.text.toString()
            if (amount.isNotEmpty()) {
                Toast.makeText(this, "Expense of R$amount Saved", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show()
            }
        }

        btnUploadImage.setOnClickListener {
            Toast.makeText(this, "Image upload feature coming soon!", Toast.LENGTH_SHORT).show()
        }
    }
}