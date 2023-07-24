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

open class CoffeeInCart(private val coffee: Coffee): Coffee(coffee.getName(), coffee.getImageFilename(), coffee.getPrice()){
    protected var quantity=1
    protected var currentSize=1 //1 là size nhỏ, 2 là size vừa, 3 là size lớn

    protected var singlePrice=coffee.getPrice()

    constructor(other: CoffeeInCart): this(other as Coffee){
        quantity=other.getquantity()
        currentSize=other.getSize()

        when (currentSize){
            1->singlePrice=coffee.getPrice()
            2->singlePrice=(coffee.getPrice().toFloat()*1.2f).toLong()
            3->singlePrice=(coffee.getPrice().toFloat()*1.3f).toLong()
        }
    }

    fun changeQuantity(newQuantity: Int){
        this.quantity=newQuantity
    }

    fun changeSize(newSize: Int){
        this.currentSize=newSize

        when (currentSize){
            1->singlePrice=coffee.getPrice()
            2->singlePrice=(coffee.getPrice().toFloat()*1.2f).toLong()
            3->singlePrice=(coffee.getPrice().toFloat()*1.3f).toLong()
        }
    }

    fun getquantity(): Int{
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
    init {
        quantity=1
    }

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
        return 0
    }
}