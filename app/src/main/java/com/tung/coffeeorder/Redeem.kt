package com.tung.coffeeorder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.api.Distribution.BucketOptions.Linear
import com.tung.coffeeorder.AppController.Companion.initRedeem
import com.tung.coffeeorder.AppController.Companion.redeemCoffees
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Redeem : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.redeem_activity)



        val redeemRecyclerView=findViewById<RecyclerView>(R.id.redeem_recycler)
        redeemRecyclerView.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        redeemRecyclerView.adapter=RedeemAdapter(this, redeemCoffees)

        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            finish() //thoát activity này
        }
    }

    override fun onRestart() {
        super.onRestart()
        initRedeem() //cập nhật danh sách redeem
    }
}