package com.tung.coffeeorder.adapters

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.tung.coffeeorder.*
import com.tung.coffeeorder.AppController.Companion.dateFormat
import com.tung.coffeeorder.AppController.Companion.dateTimeFormat
import com.tung.coffeeorder.AppController.Companion.increaseOrders
import com.tung.coffeeorder.AppController.Companion.increaseRedeems
import com.tung.coffeeorder.AppController.Companion.numberOfRedeem

import com.tung.coffeeorder.AppController.Companion.ongoingOrders
import java.time.LocalDateTime
import java.util.*

class RedeemAdapter(activity: Activity, redeemCoffees: LinkedList<RedeemCoffee>):
    RecyclerView.Adapter<RedeemAdapter.redeemViewHolder>() {

    val activity=activity
    val redeemCoffees = redeemCoffees

    inner class redeemViewHolder(view: View): RecyclerView.ViewHolder(view){

        val coffeeName = view.findViewById<TextView>(R.id.coffeeTitle)
        val validDate = view.findViewById<TextView>(R.id.validdate)
        val imageView = view.findViewById<ImageView>(R.id.coffeeImage)
        val sizeText = view.findViewById<TextView>(R.id.size)

        val redeemBtn = view.findViewById<MaterialButton>(R.id.redeem_btn)

        fun redeem(redeemCoffee: RedeemCoffee){
            //add vào cart một ly cà phê 0đ
            //mở cart
            //nếu đủ điểm

            if (redeemCoffee.getRedeemPoints()<= User.singleton.loyalty.getCurrentPoints()){
                Toast.makeText(
                    activity,
                    "Đã đổi nước thành công",
                    Toast.LENGTH_SHORT,
                ).show()

                increaseRedeems() //tăng số redeem lên
                val order = Order(-numberOfRedeem, User.singleton.getaddress(),
                    LocalDateTime.now().format(dateTimeFormat),ArrayList<CoffeeInCart>())
                order.cart.add(redeemCoffee)
                Log.d("id",order.id.toString())
                saveOrder(order,activity) //rồi mới update
                setRedeem(order,redeemCoffee.getRedeemPoints(),activity)
                ongoingOrders.add(order) //thêm vào orders
                increaseOrders()


                val intent= Intent(activity, OrderSuccess::class.java)
                intent.putExtra("Redeem",true)
                activity.startActivity(intent)
            }
            else{
                Toast.makeText(
                    activity,
                    "Bạn chưa đủ điểm để đổi",
                    Toast.LENGTH_LONG,
                ).show()
            }

        }

        fun bind(redeemCoffee: RedeemCoffee){
            when (redeemCoffee.getSize()){
                Size.Small->{
                    sizeText.text="Size S"
                }
                Size.Medium->{
                    sizeText.text="Size M"
                }
                Size.Large->{
                    sizeText.text="Size L"
                } //hiện ra size của ly cà phê
            }
            coffeeName.text=redeemCoffee.getName()
            validDate.text="Tới "+redeemCoffee.getValidDate().format(dateFormat)

            imageView.setImageResource(AppController.imageFromCoffee(activity, redeemCoffee))

            redeemBtn.text=redeemCoffee.getRedeemPoints().toString()+ " điểm" //điểm số mất nếu redeem
            redeemBtn.setOnClickListener{
                redeem(redeemCoffee)
            }
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