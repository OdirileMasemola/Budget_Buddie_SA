package com.example.budget_buddie_sa

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

private const val COLLAPSE_WIDTH_DP = 72
private const val EXPANDED_WIDTH_DP = 200

abstract class BaseNavigationActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun setContentView(layoutResID: Int) {
        val baseLayout = layoutInflater.inflate(R.layout.activity_base_nav, null)
        val activityContainer = baseLayout.findViewById<FrameLayout>(R.id.content_frame)

        layoutInflater.inflate(layoutResID, activityContainer, true)
        super.setContentView(baseLayout)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        setupRailNavigation()
        setupDynamicRail()
    }

    private fun setupRailNavigation() {
        findViewById<ImageView>(R.id.rail_dashboard).setOnClickListener {
            if (this !is DashboardActivity) startActivity(Intent(this, DashboardActivity::class.java))
        }
        findViewById<ImageView>(R.id.rail_add).setOnClickListener {
            if (this !is AddExpenseActivity) startActivity(Intent(this, AddExpenseActivity::class.java))
        }
        findViewById<ImageView>(R.id.rail_list).setOnClickListener {
            if (this !is ExpenseListActivity) startActivity(Intent(this, ExpenseListActivity::class.java))
        }
        findViewById<ImageView>(R.id.rail_category).setOnClickListener {
            if (this !is CategoryActivity) startActivity(Intent(this, CategoryActivity::class.java))
        }
        findViewById<ImageView>(R.id.rail_budget).setOnClickListener {
            if (this !is BudgetActivity) startActivity(Intent(this, BudgetActivity::class.java))
        }
        findViewById<ImageView>(R.id.rail_profile).setOnClickListener {
            if (this !is ProfileActivity) startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun setupDynamicRail() {
        val rail = findViewById<LinearLayout>(R.id.rail_nav)
        val toggleBtn = findViewById<ImageButton>(R.id.btn_toggle_rail)

        prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        var isExpanded = prefs.getBoolean("rail_expanded", false)

        val initialWidth = if (isExpanded) EXPANDED_WIDTH_DP.toPx() else COLLAPSE_WIDTH_DP.toPx()
        rail.layoutParams.width = initialWidth
        toggleBtn.rotation = if (isExpanded) 180f else 0f
        updateLabels(isExpanded)

        toggleBtn.setOnClickListener {
            isExpanded = !isExpanded
            animateRail(rail, isExpanded)
            toggleBtn.animate().rotation(if (isExpanded) 180f else 0f).setDuration(300).start()
            prefs.edit().putBoolean("rail_expanded", isExpanded).apply()
        }
    }

    private fun animateRail(rail: View, expanding: Boolean) {
        val startWidth = rail.width
        val endWidth = if (expanding) EXPANDED_WIDTH_DP.toPx() else COLLAPSE_WIDTH_DP.toPx()

        val animator = ValueAnimator.ofInt(startWidth, endWidth)
        animator.addUpdateListener { animation ->
            val value = animation.animatedValue as Int
            val params = rail.layoutParams
            params.width = value
            rail.layoutParams = params
        }

        updateLabels(expanding)
        animator.duration = 300
        animator.start()
    }

    private fun updateLabels(show: Boolean) {
        val visibility = if (show) View.VISIBLE else View.GONE
        findViewById<TextView>(R.id.label_dashboard).visibility = visibility
        findViewById<TextView>(R.id.label_add).visibility = visibility
        findViewById<TextView>(R.id.label_list).visibility = visibility
        findViewById<TextView>(R.id.label_category).visibility = visibility
        findViewById<TextView>(R.id.label_budget).visibility = visibility
        findViewById<TextView>(R.id.label_profile).visibility = visibility
    }

    private fun Int.toPx(): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), resources.displayMetrics
    ).toInt()
}