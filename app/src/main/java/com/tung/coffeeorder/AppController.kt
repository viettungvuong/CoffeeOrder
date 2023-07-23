package com.tung.coffeeorder

import android.location.Address
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.LinkedList

class Cart private constructor(){ //private constructor để không cho gọi constructor để singleton
    companion object{
        @JvmStatic
        var singleton= Cart() //singleton
    }

    private val cartList=ArrayList<CoffeeInCart>() //giỏ hàng

    fun addToCart(coffeeInCart: CoffeeInCart){
        cartList.add(coffeeInCart)
    }

    fun removeFromCart(index: Int){
        cartList.removeAt(index)
    }

    fun getList(): ArrayList<CoffeeInCart>{
        return this.cartList
    }
}

class AppController{
    companion object{
        @JvmStatic
        var user= User() //user của session hiện tại
        val ongoingOrders=LinkedList<Order>() //danh sách các order onging
        val historyOrders=LinkedList<Order>() //danh sách các order history
        val rewardsPoint=LinkedList<Reward>() //danh sách điểm thưởng
        lateinit var ongoingAdapter: OrderAdapter
        lateinit var historyAdapter: OrderAdapter //để 2 adapter này ở đây vì hai adapter này có sự liên thông với nhau rất nhiều


        @JvmStatic
        fun checkInCart(coffeeInCart: CoffeeInCart): Int{
            val temp = Cart.singleton.getList().toList().sortedBy { it.getName() } //sort cart theo tên

            var i=0
            while (i<temp.size&&coffeeInCart.getName()<=temp[i].getName()){
                if (coffeeInCart.getName()==temp[i].getName()&&coffeeInCart.currentSize==temp[i].currentSize){
                        Log.d("Coffee size",coffeeInCart.currentSize.toString())
                        Log.d("Cart size",temp[i].currentSize.toString())
                        return i //cà phê này đã có trong giỏ hàng (trùng tên và kích thước)
                        //nếu có trả về index
                }
                i++
            }
            return -1
        }
    }


}