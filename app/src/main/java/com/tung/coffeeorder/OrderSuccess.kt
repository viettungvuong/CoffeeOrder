package com.tung.coffeeorder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.button.MaterialButton

class OrderSuccess : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.order_success)

        val trackOrderBtn = findViewById<MaterialButton>(R.id.trackBtn)
        
    }
}