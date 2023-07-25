package com.tung.coffeeorder

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.tung.coffeeorder.AppController.Companion.listCoffee

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

        val totalPoint = view.findViewById<TextView>(R.id.total_points)
        totalPoint.text=User.singleton.loyalty.getCurrentPoints().toString()

        val redeemBtn = view.findViewById<MaterialButton>(R.id.redeem_btn)
        redeemBtn.setOnClickListener{
            val intent = Intent(requireActivity(),Redeem::class.java)
            requireActivity().startActivity(intent)
        }

        return view
    }

}