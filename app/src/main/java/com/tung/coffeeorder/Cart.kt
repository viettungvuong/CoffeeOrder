package com.tung.coffeeorder

import android.content.Context
import android.util.Log
import androidx.room.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.common.reflect.TypeToken
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.tung.coffeeorder.AppController.Companion.sharedPreferences
import java.io.File
import java.io.IOException
import java.time.LocalDateTime
import java.util.*

@Entity(tableName = "cart_table")
data class Cart(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var cartList: LinkedList<CoffeeInCart>,
)

@Dao
interface CartDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCart(cart: Cart): Long

    @Update
    fun updateCart(cart: Cart)

    @Query("SELECT * FROM cart_table")
    fun getAllCarts(): List<Cart>

}

private fun getDescList(cart: Cart): LinkedList<String> { //mảng chứa mô tả các sản phẩm trong cart
    val tempList = LinkedList<String>()

    for (coffeeInCart in cart.cartList) {
        val desc = coffeeInCart.getName() + "," + coffeeInCart.getSize()
            .toString() + "," + coffeeInCart.getquantity()
            .toString() + "," + coffeeInCart.getHotOrCold().toString()
        tempList.add(desc)
    }

    return tempList
}

fun addToCart(cart: Cart, context: Context, coffeeInCart: CoffeeInCart) {
    cart.cartList.add(coffeeInCart)
    updateCart(cart,context)
}

fun removeFromCart(cart: Cart, context: Context, index: Int) {
    cart.cartList.removeAt(index)
    updateCart(cart,context)
}

fun updateCart(cart: Cart, context: Context){
    if (sharedPreferences.getBoolean("online_acc",false)){
        updateToFirebase(cart)
    }
    else{
        AppDatabase.getSingleton(context).cartDao().updateCart(cart)
    }
}

private fun updateToFirebase(cart: Cart) {
    val tempList = getDescList(cart)
    val createField = hashMapOf(
        "cart" to tempList //tạo field cho cart (array field)
    )


    val getCart = AppController.db.collection("users").document(Firebase.auth.currentUser!!.uid)
        .collection("carts")
        .document(AppController.numberOfCarts.toString())

    val deleteField = mapOf(
        "cart" to FieldValue.delete()
    )

    getCart.update(deleteField) //update firebase

    getCart
        .get()
        .addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful()) {
                getCart.set(createField) //thêm cart
            }
        })
}


//class Cart() {
//
//    private var cartList= LinkedList<CoffeeInCart>() //giỏ hàng của cart
//    companion object{
//        @JvmStatic
//        var singleton= Cart() //singleton
//    }
//
//    constructor(otherCart: Cart): this(){
//        cartList=otherCart.cartList
//    }
//
//
//    private fun getDescList(): LinkedList<String> { //mảng chứa mô tả các sản phẩm trong cart
//        val tempList = LinkedList<String>()
//
//        for (coffeeInCart in cartList){
//            val desc=coffeeInCart.getName()+","+coffeeInCart.getSize().toString()+","+coffeeInCart.getquantity().toString()+","+coffeeInCart.getHotOrCold().toString()
//            tempList.add(desc)
//        }
//
//        return tempList
//    }
//
//
//    fun addToCart(context: Context, coffeeInCart: CoffeeInCart){
//        cartList.add(coffeeInCart)
//        update(context)
//    }
//
//    fun removeFromCart(context: Context, index: Int){
//        cartList.removeAt(index)
//        update(context)
//    }
//
//    fun cartList: LinkedList<CoffeeInCart> {
//        return this.cartList
//    }
//
//    fun update(context: Context){
//        if (AppController.sharedPreferences.getBoolean("online_acc",false)){
//            updateToFirebase(getDescList()) //up lên firebase
//        }
//        else{
//            updateLocally(context,getDescList()) //xuất ra file
//        }
//    }
//
//    private fun updateToFirebase(tempList: LinkedList<String>){
//
//        val createField = hashMapOf(
//            "cart" to tempList //tạo field cho cart (array field)
//        )
//
//
//        val getCart = AppController.db.collection("users").document(Firebase.auth.currentUser!!.uid)
//            .collection("carts")
//            .document(AppController.numberOfCarts.toString())
//
//        val deleteField = mapOf(
//            "cart" to FieldValue.delete()
//        )
//
//        getCart.update(deleteField) //update firebase
//
//        getCart
//            .get()
//            .addOnCompleteListener(OnCompleteListener {
//                    task->if (task.isSuccessful()) {
//                getCart.set(createField) //thêm cart
//            }
//            })
//    }
//
//    private fun updateLocally(context: Context, tempList: LinkedList<String>){
//        val file = File(context.filesDir, cartsFileName)
//        if (!file.exists()) {
//            try {
//                file.createNewFile()
//            } catch (e: IOException) {
//                Log.d("Error",e.message.toString())
//                return
//            }
//        }
//
//        try {
//            val lines =  file.readLines().toMutableList() //đọc toàn bộ dòng và lưu vào một mảng
//
//            var i = lines.size - 1
//            while (i >= 0 && lines[i].isNotBlank()) { //đọc đến khi gặp dòng trống
//                lines.removeAt(i) //xoá dòng
//                i--
//            }
//
//            lines.addAll(tempList)
//
//            //xuất từng dòng ra file, dùng jointostring với kí tự '\n' để xuống dòng
//            file.writeText(lines.joinToString("\n"))
//
//        } catch (e: Exception) {
//            Log.d("Error","Không thể xuất ra file")
//            return
//        }
//    }
//
//}