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

        requireActivity().supportFragmentManager.beginTransaction().replace(R.id.rewards_section,RewardSection()).commit()

        val rewardsRecycler=view.findViewById<RecyclerView>(R.id.rewards_recycler)
        rewardsRecycler.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        rewardsRecycler.adapter=RewardsPointAdapter(requireActivity(),AppController.rewardsPoint)

        val totalPoint = view.findViewById<TextView>(R.id.)

        return view
    }
}