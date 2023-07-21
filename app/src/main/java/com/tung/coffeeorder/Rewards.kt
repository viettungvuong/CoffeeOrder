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
    private lateinit var rewardSectionHandler: RewardSectionHandler
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.rewards_fragment, container, false)

        rewardSectionHandler=RewardSectionHandler(requireActivity(),view.findViewById(R.id.rewards_section))

        return view
    }
}