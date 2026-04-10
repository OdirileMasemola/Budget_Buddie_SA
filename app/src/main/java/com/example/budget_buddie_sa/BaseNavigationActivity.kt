package com.example.budget_buddie_sa

import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

/**
 * Base activity that provides sidebar navigation for all child activities.
 */
abstract class BaseNavigationActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var drawerLayout: DrawerLayout

    override fun setContentView(layoutResID: Int) {
        // Inflate the base layout with the drawer and content frame
        val baseLayout = layoutInflater.inflate(R.layout.activity_base_nav, null) as DrawerLayout
        val activityContainer = baseLayout.findViewById<FrameLayout>(R.id.content_frame)
        
        // Inflate the activity's specific layout into the frame
        layoutInflater.inflate(layoutResID, activityContainer, true)
        super.setContentView(baseLayout)

        // Setup Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Setup Drawer and Toggle
        drawerLayout = baseLayout
        val navView = findViewById<NavigationView>(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation clicks
        when (item.itemId) {
            R.id.nav_dashboard -> {
                if (this !is DashboardActivity) {
                    startActivity(Intent(this, DashboardActivity::class.java))
                }
            }
            R.id.nav_add_expense -> {
                if (this !is AddExpenseActivity) {
                    startActivity(Intent(this, AddExpenseActivity::class.java))
                }
            }
            R.id.nav_expenses -> {
                if (this !is ExpenseListActivity) {
                    startActivity(Intent(this, ExpenseListActivity::class.java))
                }
            }
            R.id.nav_categories -> {
                if (this !is CategoryActivity) {
                    startActivity(Intent(this, CategoryActivity::class.java))
                }
            }
            R.id.nav_budget -> {
                if (this !is BudgetActivity) {
                    startActivity(Intent(this, BudgetActivity::class.java))
                }
            }
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