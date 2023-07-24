package com.tung.coffeeorder

import android.content.Context
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
import com.tung.coffeeorder.Functions.Companion.getCurrentNoOfCarts
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
    private var idCount=0
    private var cart: ArrayList<CoffeeInCart>
    private var time: LocalDateTime
    private var address: String
    private var redeem = false //kiểm tra xem order này có phải li nước được redeem không

    private var done=false //false là ongoing, true là history

    protected var bonuspoint=0 //điểm thưởng

    constructor(cart: ArrayList<CoffeeInCart>, time: LocalDateTime, address: String, idCount: Int, redeem: Boolean=false){
        this.cart = cart
        this.time=time
        this.address=address
        this.redeem=redeem
        this.idCount=idCount

        //tính điểm bonus
        for (coffeeInCart in getCart()){
            bonuspoint+=((coffeeInCart.getPrice()/1000).toInt())
        }
    }

    constructor(redeemCoffee: RedeemCoffee, time: LocalDateTime, address: String, redeemPoint: Int){
        this.cart= ArrayList()
        this.cart.add(redeemCoffee) //thêm redeem coffee
        this.time=time
        this.address=address
        this.redeem=true
        this.idCount=idCount
        this.bonuspoint=redeemPoint
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
    fun setDone(ongoing: LinkedList<Order>, history: LinkedList<Order>, rewards: LinkedList<Reward>, context: Context){
        ongoing.remove(this) //xoá khỏi danh sách History
        history.add(this) //thêm vào danh sách History
        val reward=Reward(this)
        rewards.add(reward) //thêm vào reward khi đơn hàng đã xong
        Log.d("Rewards size",rewards.size.toString())
        if (!redeem){
            User.singleton.loyalty.addPoints(bonuspoint) //thêm điểm loyalty
            User.singleton.loyalty.increaseLoyaltyCard(cart.size) //tăng điểm theo số ly đã có
        }
        else{
            User.singleton.loyalty.removePoints(bonuspoint) //trừ điểm sau khi redeem
        }
        done=true

        updateDone(context) //update done sẽ khác là phải chỉnh file chứ không phải thêm vào file

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

    fun updateDone(context: Context){
        if (AppController.sharedPreferences.getBoolean("online_acc",false)){
            updateToFirebase() //up lên firebase
        }
        else{
            updateDoneLocally(context) //xuất ra file
        }
    }

    fun update(context: Context){
        if (AppController.sharedPreferences.getBoolean("online_acc",false)){
            updateToFirebase() //up lên firebase
        }
        else{
            updateLocally(context) //xuất ra file
        }
    }

    private fun updateToFirebase(){
        val getOrder = AppController.db.collection("orders"+Firebase.auth.currentUser!!.uid)
            .document(idCount.toString())

        val createField = mapOf(
            "time" to time.format(DateTimeFormatter.ofPattern(dateFormat)),
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

    private fun updateLocally(context: Context){
        val file = File(context.filesDir, orderFileName)
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                Log.d("Error","Không thể xuất ra file order")
                return
            }
        }
        try {
            val writer = BufferedWriter(FileWriter(file, true)) //true là append vào file

            val temp = "$idCount,${time.format(DateTimeFormatter.ofPattern(dateFormat))},$address,$done"
            writer.write(temp)
            writer.newLine()

            writer.close()
        } catch (e: Exception) {
            Log.d("Error","Không thể xuất ra file order"+e.message.toString())
            return
        }
    }

    private fun updateDoneLocally(context: Context){
        val file = File(context.filesDir, orderFileName)
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                Log.d("Error","Không thể xuất ra file order")
                return
            }
        }
        try {
            val lines =  file.readLines().toMutableList() //đọc toàn bộ dòng và lưu vào một mảng
            lines[idCount-1]="$idCount,${time.format(DateTimeFormatter.ofPattern(dateFormat))},$address,$done" //cập nhật đúng dòng
            file.writeText(lines.joinToString("\n"))
        } catch (e: Exception) {
            Log.d("Error","Không thể xuất ra file order"+e.message.toString())
            return
        }
    }

    public fun getbonuspoint(): Int{
        return bonuspoint
    }
}