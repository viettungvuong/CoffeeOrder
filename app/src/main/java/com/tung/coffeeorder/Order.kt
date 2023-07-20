package com.tung.coffeeorder

import java.util.LinkedList

class Order(cart: LinkedList<CoffeeInCart>)
{
    private lateinit var cart: LinkedList<CoffeeInCart>

    init{
        this.cart = cart
    }

    fun addToOrder(coffeeInCart: CoffeeInCart){
        cart.add(coffeeInCart)
    }

    fun removeFromOrder(position: Int){
        cart.removeAt(position)
    }
}