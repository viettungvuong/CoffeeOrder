package com.tung.coffeeorder

import android.location.Address
import com.google.rpc.Help.Link
import com.google.type.DateTime
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class Order(cart: ArrayList<CoffeeInCart>, time: LocalDateTime, address: String)
{
    private var cart: ArrayList<CoffeeInCart>
    private var time: LocalDateTime
    private var address: String

    private var done=false //false là ongoing, true là history

    init{
        this.cart = cart
        this.time=time
        this.address=address
    }

    //up order này lên firebase
    fun updateOrder(){

    }

    fun getCart(): ArrayList<CoffeeInCart>{
        return cart
    }

    fun gettime(): LocalDateTime{
        return time
    }

    fun getaddress(): String{
        return address
    }

    //đánh dấu là đã xong
    fun setDone(ongoing: LinkedList<Order>, history: LinkedList<Order>, rewards: LinkedList<Reward>){
        ongoing.remove(this) //xoá khỏi danh sách History
        history.add(this) //thêm vào danh sách History
        val reward=Reward(this)
        rewards.add(reward) //thêm vào reward khi đơn hàng đã xong
        AppController.user.loyalty.addPoints(reward.calculateBonusPoint())
        done=true
    }

    fun totalPrice(): Long{
        var res=0L
        for (coffeeInCart in cart){
            res+=coffeeInCart.calculatePrice()
        }
        return res
    }
}