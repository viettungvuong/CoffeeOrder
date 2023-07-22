package com.tung.coffeeorder

class Reward(order: Order) {
    private var order=order

    fun getOrder(): Order{
        return order
    }

    fun calculateBonusPoint(): Long{
        var res=0L
        for (coffeeInCart in order.getCart()){
            res+=(coffeeInCart.getPrice()/1000)
        }
        return res
    }

    //up lên firebase
    fun updateReward(){

    }
}