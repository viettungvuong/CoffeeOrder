package com.tung.coffeeorder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.tung.coffeeorder.Functions.Companion.reformatNumber

class CoffeeView(coffee: Coffee) : Fragment() {
    val coffee = coffee

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.coffee_view, container, false)

        val coffeeText = view.findViewById<TextView>(R.id.coffee_title)
        coffeeText.text = coffee.getName()

        val priceText = view.findViewById<TextView>(R.id.coffee_price)
        priceText.text = reformatNumber(coffee.getPrice()) + " VNĐ"

        val imageView = view.findViewById<ImageView>(R.id.coffee_image)
        imageView.setImageResource(Functions.imageFromCoffee(requireContext(), coffee))

        val inflater = LayoutInflater.from(requireContext())

        //ta sẽ thế vào vị trí numberPicker là custom view NumberPicker của chúng ta
        val numberPickerLayout = view.findViewById<LinearLayout>(R.id.numberPicker)
        numberPickerLayout.removeAllViews() //xoá hết mọi view trong linearLayout này
        val numberPicker = NumberPicker(requireContext(), inflater, coffee)
        numberPickerLayout.addView(numberPicker)
        //rồi bây giờ ta thêm customView vào vị trí linearLayout

        //tương tự với sizePicker
        val sizePickerLayout = view.findViewById<LinearLayout>(R.id.sizePicker)
        sizePickerLayout.removeAllViews()
        val sizePicker = SizePicker(requireContext(), inflater, coffee)
        sizePickerLayout.addView(sizePicker)

        return view
    }
}
