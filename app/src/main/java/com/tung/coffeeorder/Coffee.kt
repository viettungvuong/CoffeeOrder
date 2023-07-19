package com.tung.coffeeorder

import android.media.Image

class Coffee(coffeeName: String, imageFilename: String, price: Long) {
    private var name=coffeeName
    private var image=imageFilename
    private var price=price

    fun getName(): String{
        return this.name
    }

    fun getImageFilename(): String{
        return this.image
    }

    fun getPrice(): Long{
        return this.price
    }
}