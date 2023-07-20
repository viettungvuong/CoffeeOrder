package com.tung.coffeeorder

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.rpc.Help.Link
import java.util.LinkedList

class CartAdapter(activity: Activity, cartList: LinkedList<CoffeeInCart>): RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    var activity: Activity
    var cartList: LinkedList<CoffeeInCart>

    init {
        this.activity=activity
        this.cartList=cartList
    }

    inner class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(coffeeInCart: CoffeeInCart){

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(activity).inflate(
            R.layout.cart_preview, //lấy coffee_view làm view cho adapter
            parent,false
        )
        return CartViewHolder(view)//trả về cart view holder ứng với layout
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartList[position])
    }
}