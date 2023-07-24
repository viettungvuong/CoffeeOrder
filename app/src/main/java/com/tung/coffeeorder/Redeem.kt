package com.tung.coffeeorder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.api.Distribution.BucketOptions.Linear
import com.tung.coffeeorder.AppController.Companion.redeemCoffees
import com.tung.coffeeorder.Functions.Companion.initRedeem

class Redeem : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.redeem_activity)

        val redeemRecyclerView=findViewById<RecyclerView>(R.id.redeem_recycler)
        redeemRecyclerView.layoutManager=LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        redeemRecyclerView.adapter=RedeemAdapter(this, redeemCoffees)
    }

    override fun onResume() {
        super.onResume()
        initRedeem()
    }
}