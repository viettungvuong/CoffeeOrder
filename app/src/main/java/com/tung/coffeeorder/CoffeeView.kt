package com.tung.coffeeorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.tung.coffeeorder.Functions.Companion.reformatNumber

class CoffeeView(coffee: Coffee): Fragment() {
    val coffee=coffee
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.coffee_view,container,false)

        val coffeeText = view.findViewById<TextView>(R.id.coffee_title)
        coffeeText.text=coffee.getName()

        val priceText = view.findViewById<TextView>(R.id.coffee_price)
        priceText.text=reformatNumber(coffee.getPrice())+" VNĐ"

        val imageView = view.findViewById<ImageView>(R.id.coffee_image)
        imageView.setImageResource(Functions.imageFromCoffee(requireContext(), coffee)) //đặt hình ảnh

        return view
    }
}