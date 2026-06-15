package com.example.budget_buddie_sa.util

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.example.budget_buddie_sa.R
import com.example.budget_buddie_sa.data.model.Badge

object BadgeUIHelper {

    /**
     * Maps reward type to drawable resource.
     */
    fun getBadgeBackgroundRes(badge: Badge?): Int {
        if (badge == null || !badge.isUnlocked) {
            return R.drawable.bg_badge_locked
        }
        return when (badge.rewardType.uppercase()) {
            "GOLD" -> R.drawable.bg_badge_gold
            "SILVER" -> R.drawable.bg_badge_silver
            "BRONZE" -> R.drawable.bg_badge_bronze
            else -> R.drawable.bg_badge_bronze
        }
    }

    /**
     * Updates the badge visual components based on tier and unlock status.
     * @param badge The badge data
     * @param ivIcon The ImageView for the icon (star or trophy)
     * @param viewBg The background view for the badge circle/medal
     * @param viewGloss The gloss/shine overlay view
     */
    fun updateBadgeUI(badge: Badge?, ivIcon: ImageView, viewBg: View, viewGloss: View) {
        val badgeName = badge?.badgeName ?: "No Badge"
        val rewardType = badge?.rewardType?.uppercase() ?: "NONE"
        val isUnlocked = badge?.isUnlocked ?: false
        
        // 1. Reset tints/color filters to avoid "sticky" visuals
        ivIcon.imageTintList = null
        ivIcon.colorFilter = null

        // 2. Set Icon - Prefer trophy for achievements
        ivIcon.setImageResource(R.drawable.ic_trophy)

        // 3. Set Background to the container view (viewBg)
        val backgroundRes = getBadgeBackgroundRes(badge)
        viewBg.setBackgroundResource(backgroundRes)

        if (isUnlocked) {
            // Unlocked state: white icon + gloss shine
            ivIcon.imageTintList = ColorStateList.valueOf(Color.WHITE)
            viewGloss.alpha = 1.0f
            Log.d("BadgeUIHelper", "UI Update [UNLOCKED]: name=$badgeName, tier=$rewardType, drawableRes=$backgroundRes")
        } else {
            // Locked state: grey icon + no gloss
            ivIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#C4B8DC"))
            viewGloss.alpha = 0f
            Log.d("BadgeUIHelper", "UI Update [LOCKED]: name=$badgeName, tier=$rewardType")
        }
    }
}
