package com.tung.coffeeorder

import android.app.Activity
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
        var drawable=activity.getDrawable(R.drawable.coffee_logo)!!

        fun bind(position: Int){

            if (User.singleton.loyalty.getLoyaltyCardCount()<position+1){
                drawable.alpha=155 //làm mờ hình ảnh cái ly nếu như vị trí ly hơn số điểm đã tích được
            }
            else{
                drawable.alpha=255
            }

            view.findViewById<ImageView>(R.id.rewardCup).setImageDrawable(drawable) //đặt hình cái ly vào
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