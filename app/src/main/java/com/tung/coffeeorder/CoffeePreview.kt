package com.tung.coffeeorder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class CoffeePreview @JvmOverloads constructor(context: Context, coffee: Coffee): LinearLayout(context) {
    var coffeeImage: ImageView
    var coffeeText: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.coffee_view,this,true)

        coffeeImage=findViewById(R.id.coffeeImage)
        coffeeText=findViewById(R.id.coffeeName)

    }
}