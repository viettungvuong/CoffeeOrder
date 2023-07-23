package com.tung.coffeeorder

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.rpc.Help.Link
import com.tung.coffeeorder.Functions.Companion.imageFromCoffee
import com.tung.coffeeorder.Functions.Companion.reformatNumber
import java.util.LinkedList

class CartAdapter(activity: Activity, cartList: ArrayList<CoffeeInCart>): RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    var activity: Activity
    var cartList: ArrayList<CoffeeInCart>

    init {
        this.activity=activity
        this.cartList=cartList
    }

    inner class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val coffeeName=view.findViewById<TextView>(R.id.coffeeTitle)
        val coffeePriceText = view.findViewById<TextView>(R.id.price)
        val coffeeImage=view.findViewById<ImageView>(R.id.coffeeImage)
        val coffeeQuantity=view.findViewById<TextView>(R.id.quantity)
        val coffeeSize=view.findViewById<TextView>(R.id.size)

        fun bind(coffeeInCart: CoffeeInCart){
            coffeeName.text=coffeeInCart.getName()
            coffeePriceText.text=reformatNumber(coffeeInCart.calculatePrice())
            coffeeQuantity.text="Số lượng: "+coffeeInCart.getquantity().toString()
            when (coffeeInCart.getSize()){
                1->{
                    coffeeSize.text="Size S"
                }
                2->{
                    coffeeSize.text="Size M"
                }
                3->{
                    coffeeSize.text="Size L"
                } //hiện ra size của ly cà phê
            }

            coffeeImage.setImageResource(imageFromCoffee(activity,coffeeInCart)) //đặt hình ảnh
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