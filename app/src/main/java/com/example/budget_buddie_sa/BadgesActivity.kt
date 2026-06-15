package com.example.budget_buddie_sa

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.budget_buddie_sa.adapter.BadgeAdapter
import com.example.budget_buddie_sa.data.model.Badge
import com.example.budget_buddie_sa.util.BadgeUIHelper
import com.example.budget_buddie_sa.viewmodel.BadgeViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BadgesActivity : BaseNavigationActivity() {

    private val viewModel: BadgeViewModel by viewModels()
    private lateinit var adapter: BadgeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_badges)

        supportActionBar?.title = "Badges"

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = BadgeAdapter(emptyList())
        adapter.setOnItemClickListener { badge ->
            showBadgeDetailModal(badge)
        }
        val rvBadges = findViewById<RecyclerView>(R.id.rvBadges)
        rvBadges.layoutManager = LinearLayoutManager(this)
        rvBadges.adapter = adapter
    }

    private fun showBadgeDetailModal(badge: Badge) {
        val dialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.dialog_badge_detail, null)
        
        val tvName = view.findViewById<TextView>(R.id.tvModalBadgeName)
        val tvTier = view.findViewById<TextView>(R.id.tvModalBadgeTier)
        val tvDesc = view.findViewById<TextView>(R.id.tvModalBadgeDesc)
        val tvProgress = view.findViewById<TextView>(R.id.tvModalProgressText)
        val pbProgress = view.findViewById<ProgressBar>(R.id.pbModalBadgeProgress)
        val tvStatus = view.findViewById<TextView>(R.id.tvModalStatus)
        val tvDate = view.findViewById<TextView>(R.id.tvModalUnlockedDate)
        val layoutDate = view.findViewById<View>(R.id.layoutUnlockedDate)
        val ivIcon = view.findViewById<ImageView>(R.id.ivModalBadgeIcon)
        val viewBg = view.findViewById<View>(R.id.viewModalBadgeBg)
        val btnViewAll = view.findViewById<Button>(R.id.btnViewAllBadges)
        val btnClose = view.findViewById<Button>(R.id.btnCloseModal)

        // Hide "View All" on the Badges page itself
        btnViewAll.visibility = View.GONE

        tvName.text = badge.badgeName
        tvTier.text = "${badge.rewardType.lowercase().replaceFirstChar { it.uppercase() }} Badge"
        tvDesc.text = badge.description
        tvProgress.text = "${badge.currentProgress} / ${badge.targetProgress}"
        pbProgress.max = badge.targetProgress
        pbProgress.progress = badge.currentProgress
        
        BadgeUIHelper.updateBadgeUI(badge, ivIcon, viewBg, view.findViewById(R.id.viewModalBadgeGloss))
        
        if (badge.isUnlocked) {
            tvStatus.text = "Unlocked"
            tvStatus.setTextColor(Color.parseColor("#10B981"))
            layoutDate.visibility = View.VISIBLE
            val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            tvDate.text = sdf.format(Date(badge.unlockedDate ?: badge.lastUpdated))
        } else {
            tvStatus.text = "Locked"
            tvStatus.setTextColor(Color.parseColor("#EF4444"))
            layoutDate.visibility = View.GONE
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun observeViewModel() {
        viewModel.badges.observe(this) { badges ->
            if (badges != null) {
                // Sort unlocked badges to the top
                val sortedBadges = badges.sortedByDescending { it.isUnlocked }
                adapter.updateData(sortedBadges)
            }
        }
    }
}
