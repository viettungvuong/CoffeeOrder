package com.tung.coffeeorder.adapters

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tung.coffeeorder.R
import com.tung.coffeeorder.Reward
import java.util.LinkedList

class RewardsPointAdapter(activity: Activity, rewards: LinkedList<Reward>): RecyclerView.Adapter<RewardsPointAdapter.rewardViewHolder>() {
    val activity=activity
    val rewards=rewards

    inner class rewardViewHolder(view: View): RecyclerView.ViewHolder(view){
        val view=view
        val coffeesInCart = view.findViewById<LinearLayout>(R.id.coffeesInCart)

        init {
            coffeesInCart.removeAllViewsInLayout() //xoá hết view trong layout này
        }

        fun bind(reward: Reward){
            for (coffeeInCart in reward.getOrder().cart){
                val coffeeText = LayoutInflater.from(activity).inflate(R.layout.coffee_in_cart_linear,null) as TextView
                coffeeText.text=coffeeInCart.getName()

                coffeesInCart.addView(coffeeText) //thêm vào linear layout
            }


            view.findViewById<TextView>(R.id.time).text=reward.getOrder().time //đặt ngày giờ

            val pointAdded = view.findViewById<TextView>(R.id.pointAdded)
            val redeem=reward.getOrder().redeem
            if (!redeem){
                pointAdded.text="+"+reward.calculateBonusPoint().toString()+" điểm"
                pointAdded.setTextColor(Color.parseColor("#007B5E"))
            }
            else{
                pointAdded.text="-"+reward.calculateBonusPoint().toString()+" điểm"
                pointAdded.setTextColor(Color.RED)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): rewardViewHolder {
        val view = LayoutInflater.from(activity).inflate(
            R.layout.reward_adapter,
            parent,false
        )
        return rewardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return rewards.size
    }

    override fun onBindViewHolder(holder: rewardViewHolder, position: Int) {
        holder.bind(rewards[position])
    }
}