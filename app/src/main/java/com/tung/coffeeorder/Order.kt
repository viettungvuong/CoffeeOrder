package com.tung.coffeeorder

import android.location.Address
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.ktx.Firebase
import com.google.rpc.Help.Link
import com.google.type.DateTime
import com.tung.coffeeorder.AppController.Companion.carts
import com.tung.coffeeorder.AppController.Companion.dateFormat
import com.tung.coffeeorder.AppController.Companion.historyOrders
import com.tung.coffeeorder.AppController.Companion.ongoingOrders
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class Order
{

    private var cart: ArrayList<CoffeeInCart>
    private var time: LocalDateTime
    private var address: String
    private var redeem = false //kiểm tra xem order này có phải li nước được redeem không

    private var done=false //false là ongoing, true là history

    constructor(cart: ArrayList<CoffeeInCart>, time: LocalDateTime, address: String, redeem: Boolean=false){
        this.cart = cart
        this.time=time
        this.address=address
        this.redeem=redeem
    }

    constructor(redeemCoffee: RedeemCoffee, time: LocalDateTime, address: String){
        this.cart= ArrayList()
        this.cart.add(redeemCoffee) //thêm redeem coffee
        this.time=time
        this.address=address
        this.redeem=true
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
        User.singleton.loyalty.addPoints(reward.calculateBonusPoint())
        done=true
    }

    fun totalPrice(): Long{
        var res=0L
        for (coffeeInCart in cart){
            res+=coffeeInCart.calculatePrice()
        }
        return res
    }

    fun getWhetherRedeem(): Boolean{
        return redeem
    }

    fun update(){
        if (AppController.sharedPreferences.getBoolean("online_acc",false)){
            updateToFirebase() //up lên firebase
        }
        else{
            updateLocally() //xuất ra file
        }
    }

    private fun updateToFirebase(){
        val getOrder = AppController.db.collection("orders"+Firebase.auth.currentUser!!.uid)
            .document(Functions.getCurrentNoOfCarts().toString())

        val createField = mapOf(
            "time" to time,
            "address" to address,
            "done" to done.toString()
        )

        getOrder//lấy document trên firebase
            .get()
            .addOnCompleteListener(OnCompleteListener {
                    task->if (task.isSuccessful()) {
                getOrder.set(createField) //thêm order
            }
            })
    }

    private fun updateLocally(){
        val file = File("orders")
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                Log.d("Error","Không thể xuất ra file")
                return
            }
        }

        try {
            val writer = BufferedWriter(FileWriter(file, true)) //true là append vào file

            val temp = "$time,$address,$done"
            writer.write(temp)
            writer.newLine()

            writer.close()
        } catch (e: Exception) {
            Log.d("Error","Không thể xuất ra file")
            return
        }
    }


}