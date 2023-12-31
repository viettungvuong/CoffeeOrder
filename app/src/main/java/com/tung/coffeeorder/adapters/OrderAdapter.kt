package com.tung.coffeeorder.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.tung.coffeeorder.*
import com.tung.coffeeorder.AppController.Companion.reformatNumber
import java.util.*

class OrderAdapter(activity: Activity, orders: LinkedList<Order>, fragment: Fragment): RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {
    val activity=activity
    val orders=orders
    val fragment = fragment


    inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timeText = view.findViewById<TextView>(R.id.time)
        val addressText = view.findViewById<TextView>(R.id.address)
        val drinksList = view.findViewById<LinearLayout>(R.id.drinks)
        val totalPriceText = view.findViewById<TextView>(R.id.total_price)

        init {
            drinksList.removeAllViewsInLayout() //xoá hết những cái dummy view trong linearLayout này
        }

        fun drinkView(coffeeInCart: CoffeeInCart): LinearLayout{
            var linearLayout =  LayoutInflater.from(activity).inflate(R.layout.dummy_drink_order, null) //lấy mẫu linear layout có sẵn

            val drinkContent=linearLayout.findViewById<TextView>(R.id.drinkContent)
            val priceText = linearLayout.findViewById<TextView>(R.id.price)

            var sizeString = ""
            when (coffeeInCart.getSize()){
                Size.Small->{
                    sizeString="(Size S)"
                }
                Size.Medium->{
                    sizeString="(Size M)"
                }
                Size.Large->{
                    sizeString="(Size L)"
                } //hiện ra size của ly cà phê
            }
            drinkContent.text=coffeeInCart.getName()+" "+sizeString+" x"+coffeeInCart.getquantity().toString()
            priceText.text=reformatNumber(coffeeInCart.calculatePrice())+" VNĐ"

            return linearLayout as LinearLayout
        }

        fun bind(order: Order){

            timeText.text=order.time //đặt ngày tháng

            addressText.text=order.address.toString()

            val cart = order.cart
            for (drink in cart){
                drinksList.addView(drinkView(drink)) //với từng cà phê trong order này thì thêm vào view
            }

            totalPriceText.text= reformatNumber(calculateTotalPrice(order))+" VNĐ"
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
        holder.bind(orders[holder.layoutPosition])

        holder.itemView.setOnClickListener(
            View.OnClickListener {
                if (fragment is OngoingFragment){ //chỉ nhận onclick của OngoingFragment

                    setOrderDone(orders[holder.layoutPosition],
                        AppController.ongoingOrders,
                        AppController.historyOrders,
                        AppController.rewardsPoint,activity) //đánh dấu đã hoàn thành order này
                    AppController.ongoingAdapter.notifyItemRemoved(position)

                    AppController.historyAdapter.notifyItemInserted(AppController.historyOrders.size-1) //thông báo mới thêm item
                    holder.itemView.visibility= View.GONE
                    Toast.makeText(
                        activity,
                        "Đã đánh dấu đơn hàng hoàn thành",
                        Toast.LENGTH_SHORT,
                    ).show()
                }

            }
        )
    }

}