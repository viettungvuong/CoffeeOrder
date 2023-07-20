package com.tung.coffeeorder

import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.tung.coffeeorder.Functions.Companion.reformatNumber

class CoffeeView() : AppCompatActivity() {
    lateinit var coffee: Coffee

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.coffee_view)

        coffee = intent.getSerializableExtra("coffee") as Coffee

        val coffeeText = findViewById<TextView>(R.id.coffee_title)
        coffeeText.text = coffee.getName()

        val priceText = findViewById<TextView>(R.id.coffee_price)
        priceText.text = reformatNumber(coffee.getPrice()) + " VNĐ"

        val imageView = findViewById<ImageView>(R.id.coffee_image)
        imageView.setImageResource(Functions.imageFromCoffee(this, coffee))

        val inflater=LayoutInflater.from(this)

        //ta sẽ thế vào vị trí numberPicker là custom view NumberPicker của chúng ta
        val numberPickerLayout = findViewById<LinearLayout>(R.id.numberPicker)
        numberPickerLayout.removeAllViews() //xoá hết mọi view trong linearLayout này
        val numberPicker = NumberPicker(this, inflater, coffee)
        numberPickerLayout.addView(numberPicker)
        //rồi bây giờ ta thêm customView vào vị trí linearLayout

        //tương tự với sizePicker
        val sizePickerLayout = findViewById<LinearLayout>(R.id.sizePicker)
        sizePickerLayout.removeAllViews()
        val sizePicker = SizePicker(this, inflater, coffee)
        sizePickerLayout.addView(sizePicker)
    }
}
