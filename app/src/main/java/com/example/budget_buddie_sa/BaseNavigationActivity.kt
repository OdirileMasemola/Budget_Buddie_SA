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
        findViewById<LinearLayout>(R.id.nav_row_dashboard).setOnClickListener {
            navigateTo(DashboardActivity::class.java)
        }
        findViewById<LinearLayout>(R.id.nav_row_add).setOnClickListener {
            navigateTo(AddExpenseActivity::class.java)
        }
        findViewById<LinearLayout>(R.id.nav_row_list).setOnClickListener {
            navigateTo(ExpenseListActivity::class.java)
        }
        findViewById<LinearLayout>(R.id.nav_row_category).setOnClickListener {
            navigateTo(CategoryActivity::class.java)
        }
        findViewById<LinearLayout>(R.id.nav_row_budget).setOnClickListener {
            navigateTo(BudgetActivity::class.java)
        }
        findViewById<LinearLayout>(R.id.nav_row_profile).setOnClickListener {
            navigateTo(ProfileActivity::class.java)
        }
    }

    private fun <T> navigateTo(activityClass: Class<T>) {
        // First, save the collapsed state so the next activity starts minimized
        getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("rail_expanded", false)
            .apply()

        // Then, if we are not already on that page, navigate to it
        if (this::class.java != activityClass) {
            startActivity(Intent(this, activityClass))
        } else {
            // If we are already on the page, just collapse the rail visually
            val rail = findViewById<LinearLayout>(R.id.rail_nav)
            val toggleBtn = findViewById<ImageButton>(R.id.btn_toggle_rail)
            animateRail(rail, false)
            toggleBtn.animate().rotation(0f).setDuration(300).start()
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