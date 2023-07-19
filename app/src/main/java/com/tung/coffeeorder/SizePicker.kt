package com.tung.coffeeorder

import android.content.Context
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.LinearLayout
import com.google.android.material.textfield.TextInputEditText

class SizePicker(context: Context): LinearLayout(context){
    private var smallButton: ImageButton
    private var mediumButton: ImageButton
    private var largeButton: ImageButton
    init {
        val view = LayoutInflater.from(context).inflate(R.layout.pick_size,this,false)

        smallButton=findViewById(R.id.buttonSmall)
        mediumButton=findViewById(R.id.buttonMedium)
        largeButton=findViewById(R.id.buttonLarge)
    }
}