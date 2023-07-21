package com.tung.coffeeorder

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.tung.coffeeorder.Functions.Companion.imageFromCoffee

class RewardsAdapter(activity: Activity, user: User): RecyclerView.Adapter<RewardsAdapter.rewardViewHolder>() {
    var activity: Activity
    var user: User

    init {
        this.activity=activity
        this.user=user

    }

    inner class rewardViewHolder(view: View): RecyclerView.ViewHolder(view){
        val view=view
        var drawable=activity.getDrawable(R.drawable.coffee_cup)!!


        fun bind(position: Int){

            if (user.reward.getLoyaltyCardCount()<position){
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
            R.layout.reward_cup, //lấy coffee_view làm view cho adapter
            parent,false
        )
        return rewardViewHolder(view)//trả về cart view holder ứng với layout
    }

    override fun getItemCount(): Int {
        return 8
    }

    override fun onBindViewHolder(holder: rewardViewHolder, position: Int) {
        holder.bind(position)

    }
}