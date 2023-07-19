package com.tung.coffeeorder

import android.media.Image

class Coffee(coffeeName: String, imageFilename: String) {
    private var name=coffeeName
    private var image=imageFilename

    fun getName(): String{
        return this.name
    }

    fun getImageFilename(): String{
        return this.image
    }
}