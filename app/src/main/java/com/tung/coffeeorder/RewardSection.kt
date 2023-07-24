package com.tung.coffeeorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.fragment.app.Fragment

class RewardSection: Fragment() {
    private lateinit var rewardSectionHandler: RewardSectionHandler
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.rewards_cup_section, container, false)

        rewardSectionHandler=RewardSectionHandler(requireActivity(),view)

        view.setOnClickListener{
            if (User.singleton.loyalty.getLoyaltyCardCount()==8){
                User.singleton.loyalty.resetLoyaltyCard() //reset loyalty card

                //hiện chúc mừng
                val imageView = view.findViewById<ImageView>(R.id.congratsImg)
                imageView.setImageResource(R.drawable.congrats)

                val fadeInAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.fade)

                imageView.startAnimation(fadeInAnimation)
            }
        }

        return view
    }

}