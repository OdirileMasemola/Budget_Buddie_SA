package com.example.budget_buddie_sa

import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budget_buddie_sa.adapter.CategoryBreakdownAdapter
import com.example.budget_buddie_sa.adapter.ExpenseAdapter
import com.example.budget_buddie_sa.data.model.Category
import com.example.budget_buddie_sa.viewmodel.DashboardViewModel
import com.example.budget_buddie_sa.viewmodel.DateRangeType
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.chip.ChipGroup
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.NumberFormat
import java.util.*

val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()

/**
 * Dashboard screen following MVVM pattern.
 * It observes real-time data from the ViewModel and updates the UI automatically.
 */
class DashboardActivity : BaseNavigationActivity() {

    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var categoryBreakdownAdapter: CategoryBreakdownAdapter
    private lateinit var sessionManager: SessionManager

    // Views for Analysis Section
    private lateinit var pieChart: PieChart
    private lateinit var llChartLegend: LinearLayout
    private lateinit var layoutEmptyState: View
    private lateinit var rvCategoryBreakdown: RecyclerView
    private lateinit var cgDateRange: ChipGroup
    
    // Budget Analysis Views
    private lateinit var tvBudgetStatus: TextView
    private lateinit var pbAnalysisBudget: ProgressBar
    private lateinit var tvAnalysisLimit: TextView
    private lateinit var tvAnalysisRemaining: TextView
    
    // Stats Views
    private lateinit var tvStatCount: TextView
    private lateinit var tvStatAvg: TextView
    private lateinit var tvStatHighest: TextView
    private lateinit var tvStatRecent: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        sessionManager = SessionManager(this)
        supportActionBar?.title = "Dashboard"

        initViews()
        setupAdapters()
        setupPieChart()
        setupDateRangePicker()
        observeData()

        val btnAddExpense = findViewById<Button>(R.id.btnAddExpense)
        btnAddExpense.setOnClickListener {
            startActivity(Intent(this, AddExpenseActivity::class.java))
        }
        
        val tvSeeAll = findViewById<TextView>(R.id.tvSeeAll)
        tvSeeAll.setOnClickListener {
            startActivity(Intent(this, ExpenseListActivity::class.java))
        }
    }

    private fun initViews() {
        pieChart = findViewById(R.id.pieChart)
        llChartLegend = findViewById(R.id.llChartLegend)
        layoutEmptyState = findViewById(R.id.layoutEmptyState)
        rvCategoryBreakdown = findViewById(R.id.rvCategoryBreakdown)
        cgDateRange = findViewById(R.id.cgDateRange)
        
        tvBudgetStatus = findViewById(R.id.tvBudgetStatus)
        pbAnalysisBudget = findViewById(R.id.pbAnalysisBudget)
        tvAnalysisLimit = findViewById(R.id.tvAnalysisLimit)
        tvAnalysisRemaining = findViewById(R.id.tvAnalysisRemaining)
        
        tvStatCount = findViewById(R.id.tvStatCount)
        tvStatAvg = findViewById(R.id.tvStatAvg)
        tvStatHighest = findViewById(R.id.tvStatHighest)
        tvStatRecent = findViewById(R.id.tvStatRecent)
        
        setupGreeting(findViewById(R.id.tvGreeting))
    }

    private fun setupAdapters() {
        // Recent Expenses
        expenseAdapter = ExpenseAdapter(emptyList())
        val rvRecentExpenses = findViewById<RecyclerView>(R.id.rvRecentExpenses)
        rvRecentExpenses.layoutManager = LinearLayoutManager(this)
        rvRecentExpenses.adapter = expenseAdapter

        // Category Breakdown
        categoryBreakdownAdapter = CategoryBreakdownAdapter(emptyList())
        rvCategoryBreakdown.layoutManager = LinearLayoutManager(this)
        rvCategoryBreakdown.adapter = categoryBreakdownAdapter
    }

    private fun setupPieChart() {
        pieChart.apply {
            setUsePercentValues(true)
            setDrawEntryLabels(false)
            description.isEnabled = false
            isDrawHoleEnabled = true
            holeRadius = 58f
            transparentCircleRadius = 62f
            setHoleColor(Color.WHITE)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(80)
            animateY(1000, Easing.EaseInOutQuad)
            setExtraOffsets(16f, 16f, 16f, 16f)
            
            setDrawCenterText(true)
            centerText = "Spending\nBreakdown"
            setCenterTextSize(13f)
            setCenterTextColor(Color.parseColor("#2D1B6B"))
            setCenterTextTypeface(Typeface.DEFAULT_BOLD)
            
            legend.isEnabled = false
        }
    }

    private fun buildLegend(entries: List<PieEntry>, colors: List<Int>) {
        llChartLegend.removeAllViews()
        entries.forEachIndexed { index, entry ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, 8.dp, 0, 8.dp)
            }

            // Colored dot
            val dot = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(28.dp, 28.dp).also {
                    it.marginEnd = 12.dp
                }
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(colors[index])
                }
            }

            // Label with bubble background
            val label = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                text = entry.label
                setTextColor(Color.parseColor("#2D1B6B"))
                textSize = 13f
                typeface = Typeface.DEFAULT_BOLD
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 20.dp.toFloat()
                    setColor(Color.parseColor("#F3EEFF"))
                }
                setPadding(12.dp, 6.dp, 12.dp, 6.dp)
            }

            // Percentage chip on the right
            val percent = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.marginStart = 8.dp }
                text = "${entry.value.toInt()}%"
                setTextColor(Color.parseColor("#7C3AED"))
                textSize = 13f
                typeface = Typeface.DEFAULT_BOLD
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    cornerRadius = 20.dp.toFloat()
                    setColor(Color.parseColor("#EDE9FE"))
                }
                setPadding(10.dp, 4.dp, 10.dp, 4.dp)
            }

            row.addView(dot)
            row.addView(label)
            row.addView(percent)
            llChartLegend.addView(row)
        }
    }

    private fun setupDateRangePicker() {
        cgDateRange.setOnCheckedStateChangeListener { group, checkedIds ->
            when (checkedIds.firstOrNull()) {
                R.id.chipToday -> viewModel.setDateRangeType(DateRangeType.TODAY)
                R.id.chip7Days -> viewModel.setDateRangeType(DateRangeType.LAST_7_DAYS)
                R.id.chip30Days -> viewModel.setDateRangeType(DateRangeType.LAST_30_DAYS)
                R.id.chipCustom -> showCustomDatePicker()
            }
        }
    }

    private fun showCustomDatePicker() {
        val picker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select Date Range")
            .setSelection(
                androidx.core.util.Pair(
                    MaterialDatePicker.todayInUtcMilliseconds(),
                    MaterialDatePicker.todayInUtcMilliseconds()
                )
            )
            .build()

        picker.addOnPositiveButtonClickListener { range ->
            val start = range.first
            val end = range.second
            if (start != null && end != null) {
                // Adjust end to end of day
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = end
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                viewModel.setCustomDateRange(start, calendar.timeInMillis)
                Log.d("DashboardActivity", "Custom date range selected: $start to ${calendar.timeInMillis}")
            }
        }
        
        picker.addOnCancelListener { 
            // Reset to 7 days if cancelled
            findViewById<View>(R.id.chip7Days).performClick()
        }

        picker.show(supportFragmentManager, "DATE_RANGE_PICKER")
    }

    private fun observeData() {
        // --- Existing Summary Data ---
        viewModel.overallTotalSpending.observe(this) { spending ->
            findViewById<TextView>(R.id.tvTotalSpending).text = formatCurrency(spending ?: 0.0)
        }

        viewModel.thisMonthSpending.observe(this) { spending ->
            findViewById<TextView>(R.id.tvMonthTotal).text = formatCurrency(spending ?: 0.0)
        }

        viewModel.currentBudget.observe(this) { budget ->
            // Top card budget handled by progress flow below
        }

        viewModel.remainingBudget.observe(this) { remaining ->
            findViewById<TextView>(R.id.tvRemainingBudget).text = formatCurrency(remaining)
        }

        viewModel.spendingProgress.observe(this) { progress ->
            findViewById<ProgressBar>(R.id.pbBudgetTracking).progress = progress
        }

        viewModel.spendingPercentText.observe(this) { percent ->
            findViewById<TextView>(R.id.tvBudgetUsedPercent).text = "$percent%"
        }
        
        viewModel.categoryCount.observe(this) { count ->
            findViewById<TextView>(R.id.tvCategoryCount).text = "$count Active"
        }

        viewModel.recentExpenses.observe(this) { expenses ->
            expenseAdapter.updateData(expenses)
        }

        // --- New Analysis Data ---
        
        viewModel.filteredExpenses.observe(this) { expenses ->
            if (expenses.isEmpty()) {
                pieChart.visibility = View.GONE
                llChartLegend.visibility = View.GONE
                layoutEmptyState.visibility = View.VISIBLE
            } else {
                pieChart.visibility = View.VISIBLE
                llChartLegend.visibility = View.VISIBLE
                layoutEmptyState.visibility = View.GONE
            }
        }

        viewModel.categoryTotals.observe(this) { totals ->
            updatePieChart(totals)
            categoryBreakdownAdapter.updateData(totals.toList())
            Log.d("DashboardActivity", "Chart refresh: ${totals.size} categories")
        }

        viewModel.budgetAnalysis.observe(this) { analysis ->
            tvAnalysisLimit.text = formatCurrency(analysis.limit)
            tvAnalysisRemaining.text = formatCurrency(analysis.remaining)
            pbAnalysisBudget.progress = analysis.usagePercent.coerceIn(0, 100)
            
            tvBudgetStatus.text = if (analysis.hasBudget) {
                if (analysis.usagePercent >= 100) "Over Budget! (${analysis.usagePercent}%)"
                else "${analysis.usagePercent}% of budget used"
            } else {
                "No budget configured"
            }
        }

        viewModel.dashboardStats.observe(this) { stats ->
            tvStatCount.text = stats.expenseCount.toString()
            tvStatAvg.text = formatCurrency(stats.averageDaily)
            tvStatHighest.text = stats.highestCategory
            tvStatRecent.text = stats.mostRecentExpense
        }
    }

    private fun updatePieChart(totals: Map<Category, Double>) {
        if (totals.isEmpty()) return

        val totalAmount = totals.values.sum()
        val entries = totals.map { (category, amount) ->
            // Calculate percentage for legend display if needed, 
            // but PieChart does it internally for the chart.
            // Requirement says entry.value.toInt()% in legend.
            // PieEntry value will be the percentage if setUsePercentValues(true) 
            // is handled by formatter, but here value is absolute amount usually.
            // If setUsePercentValues(true), we should check what entry.value gives.
            // Actually, PieEntry(amount) will have amount.
            // To get percentage in legend as requested: (amount / totalAmount * 100)
            val percentage = if (totalAmount > 0) (amount / totalAmount * 100).toFloat() else 0f
            PieEntry(percentage, category.name)
        }

        val colors = totals.map { (category, _) ->
            try { Color.parseColor(category.color) } catch (e: Exception) { Color.parseColor("#7C3AED") }
        }

        val dataSet = PieDataSet(entries, "Spending").apply {
            setColors(colors)
            sliceSpace = 3f
            selectionShift = 5f
            // Disable labels on slices
            setDrawValues(false) 
        }

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.highlightValues(null)
        pieChart.invalidate()
        
        buildLegend(entries, colors)
    }

    private fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("en", "ZA"))
        return format.format(amount)
    }

    private fun spToPx(sp: Float): Float {
        return sp * resources.displayMetrics.scaledDensity
    }

    private fun setupGreeting(tvGreeting: TextView) {
        val username = sessionManager.getDisplayName() ?: "User"
        
        if (!sessionManager.hasLoggedInBefore()) {
            tvGreeting.text = "Welcome, $username 👋"
            sessionManager.setHasLoggedInBefore(true)
        } else {
            val greetings = listOf(
                "Welcome back, $username 👋",
                "Great to see you again, $username 👋",
                "Ready to manage your budget, $username? 👋",
                "Let's continue your budgeting journey, $username 👋",
                "Glad you're back, $username 👋",
                "Time to take control of your finances, $username 👋",
                "Your budget is waiting, $username 👋",
                "Back again, $username? Let's do this 👋"
            )
            tvGreeting.text = greetings.random()
        }
    }
}