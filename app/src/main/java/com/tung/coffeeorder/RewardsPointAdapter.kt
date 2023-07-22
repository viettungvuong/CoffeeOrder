package com.tung.coffeeorder

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.allViews
import androidx.recyclerview.widget.RecyclerView
import java.time.format.DateTimeFormatter
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
            for (coffeeInCart in reward.getOrder().getCart()){
                val coffeeText = LayoutInflater.from(activity).inflate(R.layout.coffee_in_cart_linear,null) as TextView
                coffeeText.text=coffeeInCart.getName()
            }

            val dateFormat = "dd-MM-yyyy HH:mm" //format ngày tháng
            view.findViewById<TextView>(R.id.drinkContent).text=reward.getOrder().gettime().format(DateTimeFormatter.ofPattern(dateFormat)).toString()

            view.findViewById<TextView>(R.id.pointAdded).text="+"+reward.calculateBonusPoint().toString()+" điểm"
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