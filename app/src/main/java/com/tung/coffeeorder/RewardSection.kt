package com.tung.coffeeorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RewardSection: Fragment() {
    private lateinit var rewardsCupAdapter: RewardsCupAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.rewards_cup_section, container, false)

        rewardsCupAdapter= RewardsCupAdapter(requireActivity(), User.singleton)
        val cupsRecycler=view.findViewById<RecyclerView>(R.id.cupsRewards)
        cupsRecycler.layoutManager=
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL,false)
        cupsRecycler.adapter=rewardsCupAdapter

        val currentPoint: TextView =view.findViewById(R.id.currentPoints)
        currentPoint.text=User.singleton.loyalty.getLoyaltyCardCount().toString()+"/8"

        view.setOnClickListener {
            if (User.singleton.loyalty.getLoyaltyCardCount()==8){
                User.singleton.loyalty.resetLoyaltyCard() //reset loyalty card
                rewardsCupAdapter.notifyItemRangeRemoved(0,8) //xoá hết
                Toast.makeText(
                    context,
                    "Đã reset hết 8 ly",
                    Toast.LENGTH_SHORT,
                ).show()

            }
        }

        return view
    }

}