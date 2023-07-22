package com.tung.coffeeorder

class Reward(order: Order) {
    private var order=order

    fun getOrder(): Order{
        return order
    }

    fun calculateBonusPoint(): Int{
        var res=0L
        for (coffeeInCart in order.getCart()){
            res+=(coffeeInCart.getPrice()/1000)
        }
        return res.toInt()
    }

    //up lên firebase
    fun updateReward(){

    }
}