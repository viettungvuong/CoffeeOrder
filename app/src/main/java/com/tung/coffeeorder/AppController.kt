package com.tung.coffeeorder

import java.util.LinkedList

class AppController{
    companion object{
        @JvmField
        val user= User() //user của session hiện tại
        val cartList=LinkedList<CoffeeInCart>() //giỏ hàng
    }

}