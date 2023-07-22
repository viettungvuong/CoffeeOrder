package com.tung.coffeeorder

import android.location.Address
import com.google.type.DateTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class Order(cart: LinkedList<CoffeeInCart>, time: LocalDateTime, address: Address)
{
    private var cart: LinkedList<CoffeeInCart>
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

    fun getCart(): LinkedList<CoffeeInCart>{
        return cart
    }

    fun gettime(): LocalDateTime{
        return time
    }

    fun getaddress(): Address{
        return address
    }
}