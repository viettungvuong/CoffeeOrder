package com.tung.coffeeorder

import android.app.Activity
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.tung.coffeeorder.Functions.Companion.reformatNumber
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class OrderAdapter(activity: Activity, orders: LinkedList<Order>): RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {
    var activity: Activity
    var orders: LinkedList<Order>

    init {
        this.activity=activity
        this.orders=orders
    }

    inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timeText = view.findViewById<TextView>(R.id.time)
        val addressText = view.findViewById<TextView>(R.id.address)
        val drinksList = view.findViewById<LinearLayout>(R.id.drinks)

        init {
            drinksList.removeAllViewsInLayout() //xoá hết những cái dummy view trong linearLayout này
        }

        fun drinkView(coffeeInCart: CoffeeInCart): LinearLayout{
            var linearLayout =  LayoutInflater.from(activity).inflate(R.layout.dummy_drink_order, null) //lấy mẫu linear layout có sẵn

            val drinkContent=linearLayout.findViewById<TextView>(R.id.drinkContent)
            val priceText = linearLayout.findViewById<TextView>(R.id.price)

            var sizeString = ""
            when (coffeeInCart.currentSize){
                1->sizeString="(size S)"
                2->sizeString="(size M)"
                3->sizeString="(size L)"
            }
            drinkContent.text=coffeeInCart.getName()+" "+sizeString+" x"+coffeeInCart.quantity.toString()

            priceText.text=reformatNumber(coffeeInCart.calculatePrice())+" VNĐ"

            return linearLayout as LinearLayout
        }

        fun bind(order: Order){
            val dateFormat = "dd-MM-yy HH:mm" //format ngày tháng
            timeText.text=order.gettime().format(DateTimeFormatter.ofPattern(dateFormat)).toString()

            addressText.text=order.getaddress().toString()

            val cart = order.getCart()
            for (drink in cart){
                drinksList.addView(drinkView(drink)) //với từng cà phê trong order này thì thêm vào view
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(activity).inflate(
            R.layout.order_adapter, //lấy coffee_view làm view cho adapter
            parent,false
        )
        return OrderViewHolder(view)//trả về cart view holder ứng với layout
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }
}