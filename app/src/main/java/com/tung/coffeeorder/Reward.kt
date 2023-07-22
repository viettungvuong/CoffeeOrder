package com.tung.coffeeorder

class Reward(order: Order) {
    private var order=order

    fun getOrder(): Order{
        return order
    }

    fun calculateBonusPoint(): Int{
        var res=0
        for (coffeeInCart in order.getCart()){
            res+=(coffeeInCart.getPrice()/1000) as Int
        }
        return res
    }

    //up lên firebase
    fun updateReward(){

    }
}