package com.tung.coffeeorder

import android.media.Image
import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.time.LocalDateTime
import kotlin.math.sin

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

open class CoffeeInCart(coffee: Coffee): Coffee(
    coffee.getName(),
    coffee.getImageFilename(),
    coffee.getPrice()
){
    protected var quantity=1
    protected var currentSize=1 //1 là size nhỏ, 2 là size vừa, 3 là size lớn

    protected val singlePrice=coffee.getPrice()

    fun changeQuantity(newQuantity: Int){
        this.quantity=newQuantity
    }

    fun changeSize(newSize: Int){
        this.currentSize=newSize
    }

    fun getQuantity(): Int{
        return quantity
    }

    fun getSize(): Int{
        return currentSize
    }

    open fun calculatePrice(): Long{
        return singlePrice*quantity
    }
}

class RedeemCoffee(coffee: Coffee, private var validDate: LocalDateTime, private var restrictedSize: Int, private var points: Int): CoffeeInCart(
    coffee
){

    fun setValidDate(newDate: LocalDateTime){
        validDate =newDate
    }

    fun getValidDate(): LocalDateTime{
        return validDate
    }

    fun setSize(size: Int){
        this.currentSize=restrictedSize //size giới hạn cho redeem coffee này
    }

    fun getPoints(): Int{
        return points //điểm số để có thể redeem
    }

    override fun calculatePrice(): Long{
        if (quantity<=1){
            return 0
        }
        else{
            return singlePrice*(quantity-1)
        }
    }
}