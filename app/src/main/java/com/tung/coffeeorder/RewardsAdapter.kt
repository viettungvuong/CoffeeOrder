package com.tung.coffeeorder

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tung.coffeeorder.Functions.Companion.imageFromCoffee

class RewardsAdapter(activity: Activity): RecyclerView.Adapter<RewardsAdapter.rewardViewHolder>() {
    lateinit var activity: Activity

    init {
        this.activity=activity
    }

    inner class rewardViewHolder(view: View): RecyclerView.ViewHolder(view){
        lateinit var drawable: Drawable

        fun bind(position: Int){
            drawable=activity.getDrawable(R.drawable.coffee_cup)!!

            drawable.alpha=50 //làm mờ hình ảnh
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): rewardViewHolder {
        val view = LayoutInflater.from(activity).inflate(
            R.layout.cart_preview, //lấy coffee_view làm view cho adapter
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