package com.tung.coffeeorder

import android.media.Image
import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.time.LocalDateTime

open class Coffee(private val coffeeName: String, private val imageFilename: String, private val price: Long):
    Serializable {

    fun getName(): String {
        return coffeeName
    }

    fun getImageFilename(): String {
        return imageFilename
    }

    fun getPrice(): Long {
        return price
    }
}

class CoffeeInCart(coffee: Coffee): Coffee(
    coffee.getName(),
    coffee.getImageFilename(),
    coffee.getPrice()
){
    var quantity=1
    var currentSize=1 //1 là size nhỏ, 2 là size vừa, 3 là size lớn
    private val singlePrice=coffee.getPrice()

    fun changeQuantity(newQuantity: Int){
        this.quantity=newQuantity

    }

    fun changeSize(newSize: Int){
        this.currentSize=newSize
    }

    fun calculatePrice(): Long{
        return singlePrice*quantity
    }

}

class RedeemCoffee(coffee: Coffee, private var validDate: LocalDateTime): Coffee(
    coffee.getName(),
    coffee.getImageFilename(),
    coffee.getPrice()
){

    fun setValidDate(newDate: LocalDateTime){
        validDate =newDate
    }

    fun getValidDate(): LocalDateTime{
        return validDate
    }
}