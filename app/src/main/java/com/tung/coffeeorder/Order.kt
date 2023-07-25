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
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking

@Entity(tableName = "order_table")
data class Order(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var redeem: Boolean = false,
    var done: Boolean = false,
    var bonusPoint: Int = 0,
    var address: String,
    var time: LocalDateTime,
    var cart: LinkedList<CoffeeInCart>
)

class Converters {
    @TypeConverter
    fun fromArrayList(value: ArrayList<String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toArrayList(value: String): ArrayList<String> {
        val listType = object : TypeToken<ArrayList<String>>() {}.type
        return Gson().fromJson(value, listType)
    }
}

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order): Long

    @Update
    suspend fun updateOrder(order: Order)

    @Query("SELECT * FROM order_table WHERE id = :orderId")
    suspend fun getOrderById(orderId: Long): Order?

    @Query("SELECT * FROM order_table")
    suspend fun getAllOrders(): List<Order>

    @Query("UPDATE order_table SET done=1 WHERE id = :orderId")
    fun markDone(orderId: Long) //update trên database

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
        User.singleton.loyalty.addPoints(order.bonusPoint) //thêm điểm loyalty

        if (!initializing) { //khi gọi từ fetchOrders thì không có bước này
            for (coffeeInCart in order.cart) {
                User.singleton.loyalty.increaseLoyaltyCard(coffeeInCart.getquantity()) //tăng điểm theo số ly đã có
            }
        }

    } else {
        User.singleton.loyalty.removePoints(order.bonusPoint) //trừ điểm sau khi redeem
    }


    updateAsDone(order,context) //update done sẽ khác là phải chỉnh file chứ không phải thêm vào file

}

fun saveOrder(order: Order, context: Context){
    if (AppController.sharedPreferences.getBoolean("online_acc", false)) {
        updateToFirebase(order) //up lên firebase
    } else {
        AppDatabase.getSingleton(context).orderDao().markDone(order.id) //tiến hành query
    }
}

fun updateAsDone(order: Order, context: Context) {
    order.done = true
    if (AppController.sharedPreferences.getBoolean("online_acc", false)) {
        updateToFirebase(order) //up lên firebase
    } else {
        runBlocking {
            AppDatabase.getSingleton(context).orderDao().insertOrder(order) //tiến hành query
        }
    }
}

private fun updateToFirebase(order: Order) {

    var createField = mapOf<String, Any>()
    if (!order.redeem) {
        val getOrder = AppController.db.collection("orders" + Firebase.auth.currentUser!!.uid)
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
        val getOrder = AppController.db.collection("orders" + Firebase.auth.currentUser!!.uid)
            .document((-order.id).toString())
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