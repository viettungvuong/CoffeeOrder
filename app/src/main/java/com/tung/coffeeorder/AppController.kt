package com.tung.coffeeorder

import android.util.Log
import java.util.LinkedList

class AppController{
    companion object{
        @JvmField
        val user= User() //user của session hiện tại
        val cartList=ArrayList<CoffeeInCart>() //giỏ hàng
        val orders=LinkedList<Order>() //danh sách các order


        @JvmStatic
        fun checkInCart(coffeeInCart: CoffeeInCart): Int{
            val temp = cartList.toList().sortedBy { it.getName() } //sort cart theo tên

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