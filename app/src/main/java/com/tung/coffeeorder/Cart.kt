package com.tung.coffeeorder

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.tung.coffeeorder.AppController.Companion.cartList
import com.tung.coffeeorder.AppController.Companion.orders
import com.tung.coffeeorder.AppController.Companion.user
import com.tung.coffeeorder.Functions.Companion.reformatNumber
import java.time.LocalDateTime

class Cart: AppCompatActivity() {
    var totalPrice=0L

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

        val checkoutBtn=findViewById<MaterialButton>(R.id.checkoutBtn)
        checkoutBtn.setOnClickListener(
            View.OnClickListener {
                //thêm vào một order
                val temp=cartList
                orders.add(Order(temp, LocalDateTime.now(), user.getaddress())) //thêm vào orders

                //xoá hết giỏ hàng khi đã checkout
                cartList.clear()

                val intent = Intent(this,OrderSuccess::class.java)
                startActivity(intent) //mở order success
            }
        )
    }

    //mỗi lần mở đi mở lại cái activity này thì sẽ cập nhật giá
    override fun onResume() {
        super.onResume()
        updateCartPrice(findViewById(R.id.totalPrice))
    }

    fun updateCartPrice(totalPriceText: TextView){
        for (coffeeInCart in cartList){
            totalPrice+=coffeeInCart.calculatePrice()
        }
        totalPriceText.text=reformatNumber(totalPrice)+" VNĐ"
    }
}