package com.tung.coffeeorder.adapters

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tung.coffeeorder.*
import com.tung.coffeeorder.AppController.Companion.imageFromCoffee
import com.tung.coffeeorder.AppController.Companion.reformatNumber
import java.util.LinkedList

class CartAdapter(activity: Activity, cartList: LinkedList<CoffeeInCart>): RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    var activity: Activity
    var cartList: LinkedList<CoffeeInCart>

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
            coffeePriceText.text=reformatNumber(coffeeInCart.calculatePrice())+" VNĐ"
            coffeeQuantity.text="Số lượng: "+coffeeInCart.getquantity().toString()
            when (coffeeInCart.getSize()){
                Size.Small->{
                    coffeeSize.text="Size S"
                }
                Size.Medium->{
                    coffeeSize.text="Size M"
                }
                Size.Large->{
                    coffeeSize.text="Size L"
                } //hiện ra size của ly cà phê
            }

            coffeeSize.text=coffeeSize.text.toString()+if (coffeeInCart.getHotOrCold()==HotCold.Cold) " |Cold" else " |Hot"
            coffeeSize.text=coffeeSize.text.toString()+if (coffeeInCart.getShot()==Shot.Single) " |Single" else " |Double"


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