package com.tung.coffeeorder

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText

class NumberPicker(context: Context, inflater: LayoutInflater, coffee: Coffee): LinearLayout(context){
    var plusButton: ImageButton
    var minusButton: ImageButton
    private var numberEditText: TextInputEditText
    private var coffeeInCart: CoffeeInCart
    init {
        inflater.inflate(R.layout.number_picker,this,true)

        plusButton=findViewById(R.id.plusButton)
        minusButton=findViewById(R.id.minusButton)

        numberEditText=findViewById(R.id.currentNumber)
        numberEditText.setText("1")
        //thêm onchangetextlistener nữa

        coffeeInCart=CoffeeInCart(coffee) //class cho phép tuỳ biến cafe đang chọn

        plusButton.setOnClickListener(plusButtonClick())
        minusButton.setOnClickListener(minusButtonClick())
    }

    fun plusButtonClick(): OnClickListener{
        return OnClickListener {
            coffeeInCart.changeQuantity(++coffeeInCart.quantity) //tăng quantity
            numberEditText.setText(coffeeInCart.quantity.toString())
        }
    }

    fun minusButtonClick(): OnClickListener{
        return OnClickListener {
            if (coffeeInCart.quantity<=1){
                Toast.makeText(
                    context,
                    "Không thể giảm số lượng thêm nữa",
                    Toast.LENGTH_SHORT,
                ).show()
            }
            else {
                coffeeInCart.changeQuantity(--coffeeInCart.quantity) //giảm quantity
                numberEditText.setText(coffeeInCart.quantity.toString())
            }
        }
    }
}