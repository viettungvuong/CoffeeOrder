package com.tung.coffeeorder

class Reward(coffeeInCart: CoffeeInCart) {
    private var coffeeInCart=coffeeInCart

    fun getCoffeeInCart(): CoffeeInCart{
        return coffeeInCart
    }

    fun calculateBonusPoint(): Int{
        return (coffeeInCart.getPrice()/1000) as Int
    }
}