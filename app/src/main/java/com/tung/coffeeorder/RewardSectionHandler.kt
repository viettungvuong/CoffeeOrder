package com.tung.coffeeorder

import android.app.Activity
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RewardSectionHandler(activity: Activity, rewardSection: View) {
    init {
        val cupsRecycler=rewardSection.findViewById<RecyclerView>(R.id.cupsRewards)
        cupsRecycler.layoutManager=
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false)
        cupsRecycler.adapter=RewardsCupAdapter(activity,AppController.user)

        val currentPoint: TextView =rewardSection.findViewById(R.id.currentPoints)
        currentPoint.text=AppController.user.loyalty.getCurrentPoints().toString()+"/8"
    }
}