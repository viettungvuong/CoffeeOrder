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

    @Query("DELETE FROM cart_table WHERE id=:cartId")
    fun deleteCart(cartId: Int)

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

fun deleteCart(cart: Cart, context: Context){
    if (sharedPreferences.getBoolean("online_acc",false)){
        AppController.db.collection("users").document(Firebase.auth.currentUser!!.uid)
            .collection("carts")
            .document(cart.id.toString())
            .delete() //xoá document
    }
    else{
        AppDatabase.getSingleton(context).cartDao().deleteCart(cart.id)
    }
}