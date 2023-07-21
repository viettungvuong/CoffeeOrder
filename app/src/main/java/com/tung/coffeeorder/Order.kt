package com.tung.coffeeorder

import java.util.LinkedList

class Order(cart: LinkedList<CoffeeInCart>)
{
    private lateinit var cart: LinkedList<CoffeeInCart>

    init{
        this.cart = cart
    }

    //up order này lên firebase
    fun updateOrders(){

    }
}