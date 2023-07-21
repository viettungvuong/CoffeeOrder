package com.tung.coffeeorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class RewardSection: Fragment() {
    private lateinit var rewardSectionHandler: RewardSectionHandler
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.rewards_section, container, false)

        rewardSectionHandler=RewardSectionHandler(requireActivity(),view)

        return view
    }
}