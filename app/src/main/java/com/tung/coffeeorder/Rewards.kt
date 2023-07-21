package com.tung.coffeeorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tung.coffeeorder.Functions.Companion.listCoffee

class Rewards: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.rewards_fragment, container, false)
        val rewardView: RecyclerView =view.findViewById(R.id.cupsRewards) //phần reward section
        rewardView.layoutManager=
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
        rewardView.adapter=RewardsAdapter(requireActivity(),AppController.user)

        val currentPoint: TextView =view.findViewById(R.id.currentPoints)
        currentPoint.text=AppController.user.reward.getCurrentPoints().toString()+" /8"

        return view
    }
}