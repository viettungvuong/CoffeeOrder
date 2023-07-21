package com.tung.coffeeorder

import android.app.Activity
import android.view.View
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RewardSectionHandler(activity: Activity, rewardSection: View) {
    init {
        val cupsRecycler=rewardSection.findViewById<RecyclerView>(R.id.cupsRewards)
        cupsRecycler.layoutManager=
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false)
        cupsRecycler.adapter=RewardsAdapter(activity,AppController.user)

        val currentPoint: TextView =rewardSection.findViewById(R.id.currentPoints)
        currentPoint.text=AppController.user.reward.getCurrentPoints().toString()+"/8"
    }
}