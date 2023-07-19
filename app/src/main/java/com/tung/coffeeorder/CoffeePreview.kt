package com.tung.coffeeorder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.tung.coffeeorder.Functions.Companion.getDownloadUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CoffeePreview @JvmOverloads constructor(context: Context, coffee: Coffee): LinearLayout(context) {
    private var coffee: Coffee
    private var coffeeImage: ImageView
    private var coffeeText: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.coffee_view,this,true)

        coffeeImage=findViewById(R.id.coffeeImage)
        coffeeText=findViewById(R.id.coffeeName)
        this.coffee=coffee

    }

    fun setText(coffee: Coffee){
        coffeeText.text=coffee.getName()
    }

    fun setImage(coffee: Coffee){
        val coffeeUrl = coffee.getImageUrl()
        val scope = CoroutineScope(Dispatchers.Main) //chạy đoạn code trong Coroutine cho suspend fun
        scope.launch {
            try{
                val imageFromStorage = getDownloadUrl(coffeeUrl)
                Glide.with(context)
                    .load(imageFromStorage)
                    .into(coffeeImage) //lấy hình ảnh từ firebase storage
            }catch (exception: Exception) {
                println("Không thể tải được file ảnh")
            }
        }

    }
}