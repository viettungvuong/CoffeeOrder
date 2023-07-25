package com.tung.coffeeorder

import android.content.Context
import android.location.Address
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.ktx.Firebase
import com.google.rpc.Help.Link
import com.google.type.DateTime
import com.tung.coffeeorder.AppController.Companion.carts
import com.tung.coffeeorder.AppController.Companion.dateFormat
import com.tung.coffeeorder.AppController.Companion.dateTimeFormat
import com.tung.coffeeorder.AppController.Companion.historyOrders
import com.tung.coffeeorder.AppController.Companion.ongoingOrders
import com.tung.coffeeorder.AppController.Companion.getCurrentNoOfCarts
import com.tung.coffeeorder.AppController.Companion.numberOfRedeem
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
    lateinit var cart: ArrayList<CoffeeInCart>
    lateinit var time: LocalDateTime
    lateinit var address: String

    private var redeem = false //kiểm tra xem order này có phải li nước được redeem không
    private var done=false //false là ongoing, true là history

    protected var bonuspoint=0 //điểm thưởng

    constructor() //default constructor

    constructor(cart: ArrayList<CoffeeInCart>, time: LocalDateTime, address: String, idCount: Int){
        this.cart = cart
        this.time=time
        this.address=address
        this.idCount=idCount

        //tính điểm bonus
        for (coffeeInCart in getcart()){
            bonuspoint+=((coffeeInCart.calculatePrice()/1000).toInt())
        }
    }

    constructor(redeemCoffee: RedeemCoffee, time: LocalDateTime, address: String, redeemPoint: Int, idCount: Int=0){
        this.redeem=true //đánh dấu là redeem

        this.cart= ArrayList()
        this.cart.add(redeemCoffee) //thêm redeem coffee
        //cart này là chỉ chưa duy nhất mỗi redeemCoffee
        this.time=time
        this.address=address

        if (idCount==0) {
            this.idCount = numberOfRedeem //vì redeem không quan trọng id nên ta cho id của nó đại là dấu âm
        }
        else{
            this.idCount=idCount
        }
        this.bonuspoint=redeemPoint
    }

    fun getcart(): ArrayList<CoffeeInCart>{
        return cart
    }

    fun gettime(): LocalDateTime{
        return time
    }

    fun getaddress(): String{
        return address
    }

    //đánh dấu là đã xong
    fun setDone(ongoing: LinkedList<Order>, history: LinkedList<Order>, rewards: LinkedList<Reward>, context: Context, initializing: Boolean=false){
        ongoing.remove(this) //xoá khỏi danh sách History
        history.add(this) //thêm vào danh sách History
        val reward=Reward(this)
        rewards.add(reward) //thêm vào reward khi đơn hàng đã xong

        //nếu là redeem
        if (!redeem){
            User.singleton.loyalty.addPoints(bonuspoint) //thêm điểm loyalty

            if (!initializing){ //khi gọi từ fetchOrders thì không có bước này
                for (coffeeInCart in cart){
                    User.singleton.loyalty.increaseLoyaltyCard(coffeeInCart.getquantity()) //tăng điểm theo số ly đã có
                }
            }

        }
        else{
            User.singleton.loyalty.removePoints(bonuspoint) //trừ điểm sau khi redeem
        }


        updateDone(context) //update done sẽ khác là phải chỉnh file chứ không phải thêm vào file

    }

    fun totalPrice(): Long{
        var res=0L
        for (coffeeInCart in cart){
            res+=coffeeInCart.calculatePrice()
        }
        return res
    }

    fun getbonuspoint(): Int{
        return bonuspoint
    }

    fun getWhetherRedeem(): Boolean{
        return redeem
    }

    fun updateDone(context: Context){
        done=true
        if (AppController.sharedPreferences.getBoolean("online_acc",false)){
            updateToFirebase() //up lên firebase
        }
        else{
            if (!redeem){
                updateDoneLocally(context) //xuất ra file
            }
            else{
                updateDoneRedeemLocally(context)
            }
        }
    }

    fun update(context: Context){
        if (AppController.sharedPreferences.getBoolean("online_acc",false)){
            updateToFirebase() //up lên firebase
        }
        else{
            if (!redeem){
                updateLocally(context) //xuất ra file
            }
            else{
                updateRedeemLocally(context)
            }
        }
    }

    private fun updateToFirebase(){

        var createField=mapOf<String,Any>()
        if (!redeem){
            val getOrder = AppController.db.collection("orders"+Firebase.auth.currentUser!!.uid)
                .document(idCount.toString())
            createField = mapOf(
                "redeem" to "false",
                "time" to time.format(dateTimeFormat),
                "address" to address,
                "done" to done.toString(),
            )
            getOrder//lấy document trên firebase
                .get()
                .addOnCompleteListener(OnCompleteListener {
                        task->if (task.isSuccessful()) {
                    getOrder.set(createField) //thêm order
                }
                })
        }
        else{ //nếu redeem thì format khác
            val getOrder = AppController.db.collection("orders"+Firebase.auth.currentUser!!.uid)
                .document((-idCount).toString())
            createField = mapOf(
                "redeem" to "true",
                "redeemCoffee" to cart[0].getName(),
                "redeemSize" to cart[0].getSize(),
                "redeemPoint" to bonuspoint,
                "time" to time.format(dateTimeFormat),
                "address" to address,
                "done" to done.toString(),
            )
            getOrder//lấy document trên firebase
                .get()
                .addOnCompleteListener(OnCompleteListener {
                        task->if (task.isSuccessful()) {
                    getOrder.set(createField) //thêm order
                }
                })
        }

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

            var temp="$idCount,${time.format(dateTimeFormat)},$address,$done"

            Log.d("lines",temp)

            writer.write(temp)
            writer.newLine()

            writer.close()
        } catch (e: Exception) {
            Log.d("Error","Không thể xuất ra file order"+e.message.toString())
            return
        }
    }

    private fun updateRedeemLocally(context: Context) {
        val file = File(context.filesDir, redeemFileName)
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                Log.d("Error", "Không thể xuất ra file redeem")
                return
            }
        }
        try {
            val writer = BufferedWriter(FileWriter(file, true)) //true là append vào file

            var temp ="$idCount,${cart[0].getName()},${cart[0].getSize()},$bonuspoint,${time.format(dateTimeFormat)},$address,$done"

            Log.d("redeem-lines",temp)

            writer.write(temp)
            writer.newLine()

            writer.close()
        } catch (e: Exception) {
            Log.d("Error", "Không thể xuất ra file order" + e.message.toString())
            return
        }
    }

    private fun updateDoneRedeemLocally(context: Context) {
        val file = File(context.filesDir, redeemFileName)
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                Log.d("Error", "Không thể xuất ra file redeem")
                return
            }
        }
        try {
            val lines = file.readLines().toMutableList() //đọc toàn bộ dòng và lưu vào một mảng

            lines[idCount] =
                "$idCount,${cart[0].getName()},${cart[0].getSize()},$bonuspoint,${time.format(dateTimeFormat)},$address,true"
            Log.d("redeem-lines",lines[idCount])
            file.writeText(lines.joinToString("\n"))
        } catch (e: Exception) {
            Log.d("Error", "Không thể xuất ra file redeem" + e.message.toString())
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
            lines[idCount-1] = "$idCount,${time.format(dateTimeFormat)},$address,true"

            Log.d("lines",lines[idCount-1])
            file.writeText(lines.joinToString("\n"))
        } catch (e: Exception) {
            Log.d("Error","Không thể xuất ra file order"+e.message.toString())
            return
        }
    }


}