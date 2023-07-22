package com.tung.coffeeorder

import android.location.Address
import com.google.type.DateTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class Order(cart: ArrayList<CoffeeInCart>, time: LocalDateTime, address: Address)
{
    private var cart: ArrayList<CoffeeInCart>
    private var time: LocalDateTime
    private var address: Address

    init{
        this.cart = cart
        this.time=time
        this.address=address
    }

    //up order này lên firebase
    fun updateOrders(){

    }

    fun getCart(): ArrayList<CoffeeInCart>{
        return cart
    }

    fun gettime(): LocalDateTime{
        return time
    }

    fun getaddress(): Address{
        return address
    }
}