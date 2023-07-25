package com.tung.coffeeorder

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.tung.coffeeorder.AppController.Companion.dateFormat
import com.tung.coffeeorder.AppController.Companion.dateTimeFormat
import com.tung.coffeeorder.AppController.Companion.increaseOrders
import com.tung.coffeeorder.AppController.Companion.increaseRedeems
import com.tung.coffeeorder.AppController.Companion.numberOfRedeem
import com.tung.coffeeorder.AppController.Companion.ongoingOrders
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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
        val pointText = view.findViewById<TextView>(R.id.points)

        val redeemBtn = view.findViewById<MaterialButton>(R.id.redeem_btn)

        fun redeem(redeemCoffee: RedeemCoffee){
            //add vào cart một ly cà phê 0đ
            //mở cart
            //nếu đủ điểm
            Log.d("Current points",User.singleton.loyalty.getCurrentPoints().toString())
            if (redeemCoffee.getRedeemPoints()<=User.singleton.loyalty.getCurrentPoints()){
                Toast.makeText(
                    activity,
                    "Đã đổi nước thành công",
                    Toast.LENGTH_SHORT,
                ).show()
                //mở cart
                val order =Order(-numberOfRedeem,User.singleton.getaddress(),
                    LocalDateTime.now().format(dateTimeFormat),LinkedList<CoffeeInCart>())
                order.cart.add(redeemCoffee)
                setRedeem(order,redeemCoffee.getRedeemPoints(),activity)

                ongoingOrders.add(order) //thêm vào orders
                saveOrder(order,activity) //rồi mới update
                increaseRedeems() //tăng số redeem lên

                val intent= Intent(activity,OrderSuccess::class.java)
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
                1->{
                    sizeText.text="Size S"
                }
                2->{
                    sizeText.text="Size M"
                }
                3->{
                    sizeText.text="Size L"
                } //hiện ra size của ly cà phê
            }
            pointText.text=redeemCoffee.getRedeemPoints().toString()+ "điểm" //điểm số mất nếu redeem
            coffeeName.text=redeemCoffee.getName()
            validDate.text=redeemCoffee.getValidDate().format(dateFormat)

            imageView.setImageResource(AppController.imageFromCoffee(activity, redeemCoffee))

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