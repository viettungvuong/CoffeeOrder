package com.tung.coffeeorder


import android.content.Context
import android.location.Address
import android.util.Log
import androidx.room.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.common.reflect.TypeToken
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
import com.google.gson.Gson
import com.tung.coffeeorder.AppController.Companion.sharedPreferences
import kotlinx.coroutines.runBlocking

@Entity(tableName = "order_table")
data class Order(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var address: String,
    var time: String,
    var cart: LinkedList<CoffeeInCart>,
    var redeem: Boolean = false,
    var done: Boolean = false,
    var bonusPoint: Int = 0,
)

class Converters {
    @TypeConverter
    fun fromListOrder(value: List<Order>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toListOrder(value: String): List<Order> {
        val listType = object : TypeToken<List<Order>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromLinkedListCoffee(value: LinkedList<CoffeeInCart>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toLinkedListCoffee(value: String): LinkedList<CoffeeInCart> {
        val listType = object : TypeToken<LinkedList<CoffeeInCart>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toLocalDateTime(value: String): LocalDateTime {
        val listType = object : TypeToken<LocalDateTime>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromOrder(value: Order): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toOrder(value: String): Order {
        val listType = object : TypeToken<Order>() {}.type
        return Gson().fromJson(value, listType)
    }
}

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrder(order: Order): Long

    @Update
    fun updateOrder(order: Order)

    @Query("SELECT * FROM order_table")
    fun getAllOrders(): List<Order>

    @Query("UPDATE order_table SET done=1 WHERE id = :orderId")
    fun markDone(orderId: Int) //update trên database

    @Query("UPDATE order_table SET redeem=1, bonusPoint= :redeemPoint  WHERE id = :orderId")
    fun markRedeem(orderId: Int, redeemPoint: Int)

}

fun setOrderDone(
    order: Order,
    ongoing: LinkedList<Order>,
    history: LinkedList<Order>,
    rewards: LinkedList<Reward>,
    context: Context,
    initializing: Boolean = false
) {
    ongoing.remove(order) //xoá khỏi danh sách History
    history.add(order) //thêm vào danh sách History
    val reward = Reward(order)
    rewards.add(reward) //thêm vào reward khi đơn hàng đã xong

    //nếu là redeem
    if (!order.redeem) {
        User.singleton.loyalty.addPoints(calculateBonusPoint(order)) //thêm điểm loyalty

        if (!initializing) { //khi gọi từ fetchOrders thì không có bước này
            for (coffeeInCart in order.cart) {
                User.singleton.loyalty.increaseLoyaltyCard(coffeeInCart.getquantity()) //tăng điểm theo số ly đã có
            }
            Log.d("Current points",User.singleton.loyalty.getCurrentPoints().toString())
        }

    } else {
        User.singleton.loyalty.removePoints(order.bonusPoint) //trừ điểm sau khi redeem
    }

    if (!initializing){
        updateAsDone(order,context) //update done sẽ khác là phải chỉnh file chứ không phải thêm vào file
    }


}

fun saveOrder(order: Order, context: Context){
    if (AppController.sharedPreferences.getBoolean("online_acc", false)) {
        updateToFirebase(order) //up lên firebase
    } else {
        AppDatabase.getSingleton(context).orderDao().insertOrder(order) //tiến hành query insert
    }
}

private fun updateAsDone(order: Order, context: Context) {
    order.done = true
    if (AppController.sharedPreferences.getBoolean("online_acc", false)) {
        updateToFirebase(order) //up lên firebase
    } else {
        AppDatabase.getSingleton(context).orderDao().markDone(order.id)
    }
}

private fun updateToFirebase(order: Order) {

    var createField = mapOf<String, Any>()
    if (!order.redeem) {
        val getOrder = AppController.db.collection("users").document(Firebase.auth.currentUser!!.uid)
            .collection("orders")
            .document(order.id.toString())
        createField = mapOf(
            "redeem" to "false",
            "time" to order.time.format(dateTimeFormat),
            "address" to order.address,
            "done" to order.done.toString(),
        )
        getOrder//lấy document trên firebase
            .get()
            .addOnCompleteListener(OnCompleteListener { task ->
                if (task.isSuccessful()) {
                    getOrder.set(createField) //thêm order
                }
            })
    } else { //nếu redeem thì format khác
        val getOrder = AppController.db.collection("users").document(Firebase.auth.currentUser!!.uid)
            .collection("orders")
            .document(order.id.toString())
        createField = mapOf(
            "redeem" to "true",
            "redeemCoffee" to order.cart[0].getName(),
            "redeemSize" to order.cart[0].getSize(),
            "redeemPoint" to order.bonusPoint,
            "time" to order.time.format(dateTimeFormat),
            "address" to order.address,
            "done" to order.done.toString(),
        )
        getOrder//lấy document trên firebase
            .get()
            .addOnCompleteListener(OnCompleteListener { task ->
                if (task.isSuccessful()) {
                    getOrder.set(createField) //thêm order
                }
            })
    }
}

fun calculateTotalPrice(order: Order): Long{
    if (!order.redeem){
        var res=0L
        for (coffeeInCart in order.cart){
            res+=coffeeInCart.calculatePrice()
        }
        return res
    }
    else{
        return 0
    }
}

fun calculateBonusPoint(order: Order): Int{
    if (!order.redeem){
        var res=0
        for (coffeeInCart in order.cart){
            res+=((coffeeInCart.calculatePrice()/1000).toInt())
        }
        return res
    }
    else{
        return order.bonusPoint
    }

}

fun setRedeem(order: Order, redeemPoint: Int, context: Context, initializing: Boolean=false){
    order.redeem=true
    order.bonusPoint=redeemPoint

    if (initializing) //nếu như được gọi lúc đang initialize đầu chương trình thì bỏ qua bước update
        return

    if (sharedPreferences.getBoolean("online_acc",false)){
        updateToFirebase(order)
    }
    else{
        AppDatabase.getSingleton(context).orderDao().markRedeem(order.id,redeemPoint)
    }

}

