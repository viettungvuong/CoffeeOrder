package com.tung.coffeeorder

class Reward(order: Order) {
    private var order=order

    fun getOrder(): Order{
        return order
    }

    fun calculateBonusPoint(): Int{
        return order.getBonusPoint()
    }

    //up lên firebase
    fun updateReward(){

    }
}