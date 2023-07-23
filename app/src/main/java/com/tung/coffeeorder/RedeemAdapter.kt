package com.tung.coffeeorder

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class RedeemAdapter(activity: Activity, redeemCoffees: LinkedList<RedeemCoffee>):
    RecyclerView.Adapter<RedeemAdapter.redeemViewHolder>() {

    val activity=activity
    val redeemCoffees = redeemCoffees

    inner class redeemViewHolder(view: View): RecyclerView.ViewHolder(view){

        fun bind(redeemCoffee: RedeemCoffee){

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): redeemViewHolder {
        val view = LayoutInflater.from(activity).inflate(
            R.layout.redeem_preview, //lấy coffee_view làm view cho adapter
            parent,false
        )
        return redeemViewHolder(view) //trả về cart view holder ứng với layout
    }

    override fun getItemCount(): Int {
        return redeemCoffees.size
    }

    override fun onBindViewHolder(holder: redeemViewHolder, position: Int) {
        holder.bind(redeemCoffees[position])
    }
}