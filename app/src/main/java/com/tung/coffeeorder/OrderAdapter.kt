package com.tung.coffeeorder

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class OrderAdapter(activity: Activity, cartList: LinkedList<CoffeeInCart>): RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {
    var activity: Activity
    var cartList: LinkedList<CoffeeInCart>

    init {
        this.activity=activity
        this.cartList=cartList
    }

    inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(coffeeInCart: CoffeeInCart){

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(activity).inflate(
            R.layout.cart_preview, //lấy coffee_view làm view cho adapter
            parent,false
        )
        return OrderViewHolder(view)//trả về cart view holder ứng với layout
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(cartList[position])
    }
}