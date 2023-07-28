package com.tung.coffeeorder

import android.media.Image
import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.sin

enum class HotCold {
    Hot,
    Cold
}

enum class Shot{
    Single,
    Double
}

enum class Size{
    Small,
    Medium,
    Large
}

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
    private var currentSize=Size.Small //1 là size nhỏ, 2 là size vừa, 3 là size lớn
    private var cold=HotCold.Cold //false là hot, true là cold
    private var shot=Shot.Single //true là single, false là double



    constructor(other: CoffeeInCart): this(other as Coffee){
        quantity=other.getquantity()
        currentSize=other.getSize()
        cold=other.getHotOrCold()
        shot=other.getShot()
    }

    fun changeQuantity(newQuantity: Int){
        this.quantity=newQuantity
    }

    fun changeSize(newSize: Int){
        when (newSize){
            1->currentSize=Size.Small
            2->currentSize=Size.Medium
            3->currentSize=Size.Large
        }
    }

    fun changeSize(newSize: Size){
        this.currentSize=newSize
    }

    fun changeHotOrCold(cold: Boolean){
        if (cold){
            this.cold=HotCold.Cold
        }
        else{
            this.cold=HotCold.Hot
        }
    }


    fun changeHotOrCold(cold: HotCold){
        this.cold=cold
    }

    fun changeShot(single: Boolean){
        if (single){
            this.shot=Shot.Single
        }
        else{
            this.shot=Shot.Double
        }
    }


    fun changeShot(single: Shot){
        this.shot=single
    }

    fun getquantity(): Int{
        return quantity
    }

    fun getSize(): Size{
        return currentSize
    }

    fun getHotOrCold(): HotCold{
        return cold
    }

    fun getShot(): Shot{
        return shot
    }

    fun getSizeNum(): Int{
        return if (currentSize==Size.Small){
            1
        } else if (currentSize==Size.Medium){
            2
        } else{
            3
        }
    }

    fun getHotOrColdBool(): Boolean{
        return cold==HotCold.Cold
    }

    fun getShotBool(): Boolean{
        return shot==Shot.Single
    }

    open fun calculatePrice(): Long{
        var singlePriceOfCoffee=0L
        when (currentSize){
            Size.Small->singlePriceOfCoffee=coffee.getSinglePrice()
            Size.Medium->singlePriceOfCoffee=(coffee.getSinglePrice().toFloat()*1.2f).toLong()
            Size.Large->singlePriceOfCoffee=(coffee.getSinglePrice().toFloat()*1.3f).toLong()
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