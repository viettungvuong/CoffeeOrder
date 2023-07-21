package com.tung.coffeeorder

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.tung.coffeeorder.Functions.Companion.reformatNumber

class SizePicker(context: Context, inflater: LayoutInflater, coffeeInCart: CoffeeInCart): LinearLayout(context){
    private var smallButton: ImageButton
    private var mediumButton: ImageButton
    private var largeButton: ImageButton
    private var coffeeInCart=coffeeInCart
    init {
        inflater.inflate(R.layout.pick_size,this,true)

        smallButton=findViewById(R.id.buttonSmall)
        mediumButton=findViewById(R.id.buttonMedium)
        largeButton=findViewById(R.id.buttonLarge)


        smallButton.setOnClickListener(smallButtonClick())
        mediumButton.setOnClickListener(mediumButtonClick())
        largeButton.setOnClickListener(largeButtonClick())
    }

    fun smallButtonClick(): OnClickListener{
        return OnClickListener {
            coffeeInCart.changeSize(1) //size nhỏ = 1
            smallButton.alpha=1f
            mediumButton.alpha=0.5f //làm mờ các nút còn lại
            largeButton.alpha=0.5f

        }
    }

    fun mediumButtonClick(): OnClickListener{
        return OnClickListener {
            coffeeInCart.changeSize(2) //size vừa = 2
            smallButton.alpha=0.5f
            mediumButton.alpha=1f
            largeButton.alpha=0.5f

        }
    }

    fun largeButtonClick(): OnClickListener{
        return OnClickListener {
            coffeeInCart.changeSize(3) //size lớn = 3
            smallButton.alpha=0.5f
            mediumButton.alpha=0.5f
            largeButton.alpha=1f


        }
    }

}