package com.tung.coffeeorder

import android.media.Image

class Coffee(coffeeName: String, image: Image) {
    private var name=coffeeName
    private var image=image

    fun getName(): String{
        return this.name
    }

    fun getImage(): Image{
        return this.image
    }
}