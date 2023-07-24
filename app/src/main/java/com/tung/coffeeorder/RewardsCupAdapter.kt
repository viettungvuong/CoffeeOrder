package com.tung.coffeeorder

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class RewardsCupAdapter(activity: Activity, user: User): RecyclerView.Adapter<RewardsCupAdapter.rewardViewHolder>() {
    val activity=activity
    val user = user

    inner class rewardViewHolder(view: View): RecyclerView.ViewHolder(view){
        val view=view

        fun bind(position: Int){
            val drawable=activity.getDrawable(R.drawable.coffee_cup)!!
            val imageView=view.findViewById<ImageView>(R.id.rewardCup)
            if (position+1>User.singleton.loyalty.getLoyaltyCardCount()){
                imageView.alpha=0.3f //làm mờ hình ảnh cái ly nếu như vị trí ly hơn số điểm đã tích được
            }
            else{
                imageView.alpha=1.0f
            }

            imageView.setImageDrawable(drawable) //đặt hình cái ly vào
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): rewardViewHolder {
        val view = LayoutInflater.from(activity).inflate(
            R.layout.reward_cup,
            parent,false
        )
        return rewardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 8 //sẽ luôn luôn thêm 8 item
    }

    override fun onBindViewHolder(holder: rewardViewHolder, position: Int) {
        holder.bind(position)
    }
}