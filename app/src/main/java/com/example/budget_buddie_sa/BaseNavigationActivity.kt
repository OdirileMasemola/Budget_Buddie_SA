package com.example.budget_buddie_sa

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

private const val COLLAPSE_WIDTH_DP = 72
// Defined sizes for the sidebar
private const val EXPANDED_WIDTH_DP = 200
// Defined sizes for the sidebar

abstract class BaseNavigationActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var drawerLayout: DrawerLayout
    private lateinit var prefs: SharedPreferences

    override fun setContentView(layoutResID: Int) {
        val baseLayout = layoutInflater.inflate(R.layout.activity_base_nav, null) as DrawerLayout
        val activityContainer = baseLayout.findViewById<FrameLayout>(R.id.content_frame)

        layoutInflater.inflate(layoutResID, activityContainer, true)
        super.setContentView(baseLayout)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // This keeps the toolbar clean but the drawer still opens via swipe.
        drawerLayout = baseLayout
        val navView = findViewById<NavigationView>(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)

        setupRailNavigation()
        setupDynamicRail() // Initialize the expandable rail logic
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
    }

  //created a new dynamic sidebar or logic

    private fun setupDynamicRail() {
        val rail = findViewById<LinearLayout>(R.id.rail_nav) // Matches the ID in your XML
        val toggleBtn = findViewById<ImageButton>(R.id.btn_toggle_rail)

        prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        var isExpanded = prefs.getBoolean("rail_expanded", false)

        // Set the initial state when the activity starts
        val initialWidth = if (isExpanded) EXPANDED_WIDTH_DP.toPx() else COLLAPSE_WIDTH_DP.toPx()
        rail.layoutParams.width = initialWidth
        toggleBtn.rotation = if (isExpanded) 180f else 0f
        updateLabels(isExpanded)

        toggleBtn.setOnClickListener {
            isExpanded = !isExpanded

            // Start the width animation
            animateRail(rail, isExpanded)

            // Turning the arrow icon
            toggleBtn.animate().rotation(if (isExpanded) 180f else 0f).setDuration(300).start()

            // Save the state to memory
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

        // Show/Hide labels during the animation
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
    }

    // creating a function to convert DP to Pixels correctly
    private fun Int.toPx(): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), resources.displayMetrics
    ).toInt()

    // created logic rail ends here

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_dashboard -> if (this !is DashboardActivity) startActivity(Intent(this, DashboardActivity::class.java))
            R.id.nav_add_expense -> if (this !is AddExpenseActivity) startActivity(Intent(this, AddExpenseActivity::class.java))
            R.id.nav_expenses -> if (this !is ExpenseListActivity) startActivity(Intent(this, ExpenseListActivity::class.java))
            R.id.nav_categories -> if (this !is CategoryActivity) startActivity(Intent(this, CategoryActivity::class.java))
            R.id.nav_budget -> if (this !is BudgetActivity) startActivity(Intent(this, BudgetActivity::class.java))
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}