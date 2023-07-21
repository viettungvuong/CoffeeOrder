package com.tung.coffeeorder

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tung.coffeeorder.AppController.Companion.cartList
import com.tung.coffeeorder.Functions.Companion.reformatNumber

class Cart: AppCompatActivity() {
    var totalPrice=0L

    override fun onStart() {
        super.onStart()

        for (coffeeInCart in cartList){
            totalPrice+=coffeeInCart.calculatePrice()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.cart_activity)
        val cartRecyclerView = findViewById<RecyclerView>(R.id.cartRecyclerView)
        cartRecyclerView.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false) //đặt recyclerView là chiều ngang
        cartRecyclerView.adapter=CartAdapter(this,cartList)

        val backBtn = findViewById<ImageButton>(R.id.back_button)
        backBtn.setOnClickListener(
            View.OnClickListener {
                finish() //quay về activity trước
            }
        )

        val totalPriceText = findViewById<TextView>(R.id.totalPrice)
        totalPriceText.text=reformatNumber(totalPrice)+" VNĐ"
    }
}