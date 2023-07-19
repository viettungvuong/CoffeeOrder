package com.tung.coffeeorder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.tung.coffeeorder.Functions.Companion.getDownloadUrl
import com.tung.coffeeorder.Functions.Companion.reformatNumber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CoffeePreview(context: Context, coffee: Coffee): LinearLayout(context) {
    private var coffee: Coffee
    private var coffeeImage: ImageView
    private var coffeeText: TextView
    private var coffePriceText: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.coffee_view,this,true)

        coffeeImage=findViewById(R.id.coffeeImage)
        coffeeText=findViewById(R.id.coffeeName)
        coffePriceText=findViewById(R.id.coffeePrice)

        this.coffee=coffee

        setImage(this.coffee)
        setText(this.coffee)
        setPrice(this.coffee)
    }

    fun setText(coffee: Coffee){
        coffeeText.text=coffee.getName()
    }

    fun setImage(coffee: Coffee){
        CoroutineScope(Dispatchers.Main).launch { //để có thể mở trong suspend fun
            try{
                Glide.with(context)
                    .load(getDownloadUrl(coffee.getImageFilename()))
                    .into(coffeeImage) //đặt hình ảnh vào imageView
            }
            catch (exception: Exception){
                print("Lỗi khi tải hình ảnh")
            }
        }
    }

    fun setPrice(coffee: Coffee){
        coffePriceText.text= reformatNumber(coffee.getPrice())
    }
}