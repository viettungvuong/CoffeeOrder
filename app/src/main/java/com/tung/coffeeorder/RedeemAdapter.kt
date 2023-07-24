package com.tung.coffeeorder

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
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
            if (redeemCoffee.getPoints()<=User.singleton.loyalty.getCurrentPoints()){
                Cart.singleton.addToCart(activity,redeemCoffee)
                Toast.makeText(
                    activity,
                    "Đã thêm nước vào giỏ hàng thành công",
                    Toast.LENGTH_SHORT,
                ).show()
//                User.singleton.loyalty.removePoints(redeemCoffee.getPoints()) //trừ điểm
                //trừ sau khi đơn hàng done nên không trừ ở đây
                ongoingOrders.add(Order(redeemCoffee, LocalDateTime.now(), User.singleton.getaddress(), redeemCoffee.getPoints())) //thêm vào ongoing orders
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
            pointText.text=redeemCoffee.getPoints().toString()+ "điểm" //điểm số mất nếu redeem
            coffeeName.text=redeemCoffee.getName()
            validDate.text=redeemCoffee.getValidDate().format(
                DateTimeFormatter.ofPattern(
                    AppController.dateFormat
                )).toString()

            imageView.setImageResource(Functions.imageFromCoffee(activity, redeemCoffee))

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