package com.example.budget_buddie_sa

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budget_buddie_sa.adapter.CategoryBreakdownAdapter
import com.example.budget_buddie_sa.utils.PdfReportGenerator
import com.example.budget_buddie_sa.viewmodel.ReportData
import com.example.budget_buddie_sa.viewmodel.ReportsViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class ReportsActivity : BaseNavigationActivity() {

    private val viewModel: ReportsViewModel by viewModels()
    private lateinit var categoryBreakdownAdapter: CategoryBreakdownAdapter
    
    private var startDate: Long = 0
    private var endDate: Long = 0

    private lateinit var tvDateRangeDisplay: TextView
    private lateinit var tvReportDateBadge: TextView
    private lateinit var layoutReportContent: LinearLayout
    private lateinit var layoutEmptyState: LinearLayout
    
    // Summary Views
    private lateinit var tvTotalSpending: TextView
    private lateinit var tvCategoriesUsed: TextView
    private lateinit var tvHighestCategory: TextView
    private lateinit var tvLowestCategory: TextView
    private lateinit var tvExpenseCount: TextView
    private lateinit var tvAvgDaily: TextView

    private val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    private val badgeDateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)

        supportActionBar?.title = getString(R.string.reports)

        initViews()
        setupAdapter()
        observeViewModel()
    }

    private fun initViews() {
        tvDateRangeDisplay = findViewById(R.id.tvDateRangeDisplay)
        tvReportDateBadge = findViewById(R.id.tvReportDateBadge)
        layoutReportContent = findViewById(R.id.layoutReportContent)
        layoutEmptyState = findViewById(R.id.layoutEmptyState)
        
        tvTotalSpending = findViewById(R.id.tvTotalSpending)
        tvCategoriesUsed = findViewById(R.id.tvCategoriesUsed)
        tvHighestCategory = findViewById(R.id.tvHighestCategory)
        tvLowestCategory = findViewById(R.id.tvLowestCategory)
        tvExpenseCount = findViewById(R.id.tvExpenseCount)
        tvAvgDaily = findViewById(R.id.tvAvgDaily)

        findViewById<LinearLayout>(R.id.btnSelectDateRange).setOnClickListener {
            showDateRangePicker()
        }

        findViewById<Button>(R.id.btnGenerateReport).setOnClickListener {
            if (startDate == 0L || endDate == 0L) {
                Toast.makeText(this, "Please select a date range first", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.generateReport(startDate, endDate)
            }
        }

        findViewById<View>(R.id.btnExportPdf).setOnClickListener {
            viewModel.reportData.value?.let { data ->
                exportAndSharePdf(data)
            }
        }
    }

    private fun setupAdapter() {
        categoryBreakdownAdapter = CategoryBreakdownAdapter(emptyList())
        val rvCategoryBreakdown = findViewById<RecyclerView>(R.id.rvCategoryBreakdown)
        rvCategoryBreakdown.layoutManager = LinearLayoutManager(this)
        rvCategoryBreakdown.adapter = categoryBreakdownAdapter
    }

    private fun showDateRangePicker() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        builder.setTitleText("Select Report Period")
        
        val picker = builder.build()
        picker.addOnPositiveButtonClickListener { range ->
            val start = range.first
            val end = range.second

            if (start != null && end != null) {
                startDate = start
                endDate = end
                
                // Adjust end date to end of day
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = endDate
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                endDate = calendar.timeInMillis

                tvDateRangeDisplay.text = "${dateFormat.format(Date(startDate))} - ${dateFormat.format(Date(endDate))}"
            }
        }
        picker.show(supportFragmentManager, "DATE_RANGE_PICKER")
    }

    private fun observeViewModel() {
        viewModel.reportData.observe(this) { report ->
            if (report != null) {
                displayReport(report)
            } else if (startDate != 0L) {
                layoutReportContent.visibility = View.GONE
                layoutEmptyState.visibility = View.VISIBLE
            }
        }
    }

    private fun displayReport(report: ReportData) {
        layoutEmptyState.visibility = View.GONE
        layoutReportContent.visibility = View.VISIBLE

        tvTotalSpending.text = currencyFormat.format(report.totalSpending)
        tvCategoriesUsed.text = report.totalCategoriesUsed.toString()
        tvHighestCategory.text = report.highestSpendingCategory
        tvLowestCategory.text = report.lowestSpendingCategory
        tvExpenseCount.text = report.numberOfExpensesRecorded.toString()
        tvAvgDaily.text = currencyFormat.format(report.averageDailySpending)
        
        tvReportDateBadge.text = "${badgeDateFormat.format(Date(report.startDate))} - ${badgeDateFormat.format(Date(report.endDate))}"

        categoryBreakdownAdapter.updateData(report.categoryBreakdown)
    }

    private fun exportAndSharePdf(reportData: ReportData) {
        val generator = PdfReportGenerator(this)
        val pdfFile = generator.generateReport(reportData)

        if (pdfFile != null) {
            val contentUri: Uri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.fileprovider",
                pdfFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, "Share Budget Report"))
        } else {
            Toast.makeText(this, "Failed to generate PDF", Toast.LENGTH_SHORT).show()
        }
    }
}
