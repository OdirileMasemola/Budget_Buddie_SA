package com.example.budget_buddie_sa.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budget_buddie_sa.R
import com.example.budget_buddie_sa.data.model.Badge
import com.example.budget_buddie_sa.util.BadgeUIHelper

class BadgeAdapter(private var badges: List<Badge>) : RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder>() {

    class BadgeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvBadgeName)
        val tvDesc: TextView = view.findViewById(R.id.tvBadgeDescription)
        val tvStatus: TextView = view.findViewById(R.id.tvBadgeStatus)
        val tvProgress: TextView = view.findViewById(R.id.tvProgressText)
        val pbProgress: ProgressBar = view.findViewById(R.id.pbBadgeProgress)
        val viewBg: View = view.findViewById(R.id.viewBadgeBg)
        val ivIcon: ImageView = view.findViewById(R.id.ivBadgeIcon)
        val ivLockedOverlay: ImageView = view.findViewById(R.id.ivLockedOverlay)
        val badgeRoot: View = view.findViewById(R.id.badgeRoot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_badge, parent, false)
        return BadgeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        val badge = badges[position]
        holder.tvName.text = badge.badgeName
        holder.tvDesc.text = badge.description
        
        holder.pbProgress.max = badge.targetProgress
        holder.pbProgress.progress = badge.currentProgress
        holder.tvProgress.text = "${badge.currentProgress} / ${badge.targetProgress}"

        // Update UI using shared helper
        BadgeUIHelper.updateBadgeUI(
            badge, 
            holder.ivIcon, 
            holder.viewBg, 
            holder.itemView.findViewById(R.id.viewBadgeGloss)
        )

        if (badge.isUnlocked) {
            holder.tvStatus.text = "Unlocked"
            holder.tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#10B981"))
            holder.tvStatus.setTextColor(Color.WHITE)
            holder.ivLockedOverlay.visibility = View.GONE
            holder.badgeRoot.alpha = 1.0f
        } else {
            holder.tvStatus.text = "Locked"
            holder.tvStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#F3EEFF"))
            holder.tvStatus.setTextColor(Color.parseColor("#7C3AED"))
            holder.ivLockedOverlay.visibility = View.VISIBLE
            holder.badgeRoot.alpha = 0.7f
        }

        holder.itemView.setOnClickListener {
            onItemClickListener?.invoke(badge)
        }
    }

    private var onItemClickListener: ((Badge) -> Unit)? = null
    fun setOnItemClickListener(listener: (Badge) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount() = badges.size

    fun updateData(newBadges: List<Badge>) {
        badges = newBadges
        notifyDataSetChanged()
    }
}
