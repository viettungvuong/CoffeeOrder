package com.tung.coffeeorder

class Coffee(coffeeName: String, imageUrl: String) {
    private var name=coffeeName
    private var image=imageUrl

    fun getName(): String{
        return this.name
    }

    fun getImageUrl(): String{
        return this.image
    }
}