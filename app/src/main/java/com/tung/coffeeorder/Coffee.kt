package com.tung.coffeeorder

import android.media.Image
import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.time.LocalDate
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

    fun getSinglePrice(): Long {
        return price
    }
}

open class CoffeeInCart(private val coffee: Coffee): Coffee(coffee.getName(), coffee.getImageFilename(), coffee.getSinglePrice()){
    protected var quantity=1
    private var currentSize=1 //1 là size nhỏ, 2 là size vừa, 3 là size lớn
    private var cold=true //false là hot, true là cold

    constructor(other: CoffeeInCart): this(other as Coffee){
        quantity=other.getquantity()
        currentSize=other.getSize()
        cold=other.getHotOrCold()
    }

    fun changeQuantity(newQuantity: Int){
        this.quantity=newQuantity
    }

    fun changeSize(newSize: Int){
        this.currentSize=newSize
    }

    fun changeHotOrCold(cold: Boolean){
        this.cold=cold
    }

    fun getquantity(): Int{
        return quantity
    }

    fun getSize(): Int{
        return currentSize
    }

    fun getHotOrCold(): Boolean{
        return cold
    }

    open fun calculatePrice(): Long{
        var singlePriceOfCoffee=0L
        when (currentSize){
            1->singlePriceOfCoffee=coffee.getSinglePrice()
            2->singlePriceOfCoffee=(coffee.getSinglePrice().toFloat()*1.2f).toLong()
            3->singlePriceOfCoffee=(coffee.getSinglePrice().toFloat()*1.3f).toLong()
        }
        return singlePriceOfCoffee*quantity
    }
}

class RedeemCoffee(coffee: Coffee, private var validDate: LocalDate, private var restrictedSize: Int, private var points: Int): CoffeeInCart(
    coffee
){
    constructor(coffee: Coffee, restrictedSize: Int, points: Int): this(coffee,
        LocalDate.now(),restrictedSize,points)

    init {
        quantity=1
    }


    fun getValidDate(): LocalDate{
        return validDate
    }


    fun getRedeemPoints(): Int{
        return points //điểm số để có thể redeem
    }

    override fun calculatePrice(): Long{
        return 0
    }
}