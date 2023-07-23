package com.tung.coffeeorder

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.location.Address
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.tung.coffeeorder.AppController.Companion.db
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
        var db= Firebase.firestore
        val storage = Firebase.storage.reference
        var listCoffee= LinkedList<Coffee>() //danh sách các coffee
        val dbCoffeeList="coffee"
        val dbCoffeeNameField="name"
        val dbCoffeeImageField="imageName"
        val dbCoffeePriceField="price"
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

class AccountFunctions {
    companion object{

        @JvmStatic
        fun signIn(activity: Activity, context: Context, username: String, password: String){
            Firebase.auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        //Log.d(TAG, "signInWithEmail:success")
                        Toast.makeText(
                            context,
                            "Đã đăng nhập thành công",
                            Toast.LENGTH_SHORT,
                        ).show()
                        val email = task.result?.user?.email
                        val name = task.result?.user?.displayName
                        val phoneNumber = task.result?.user?.phoneNumber
                        //val address
                        AppController.user.editPhoneNumber(phoneNumber)
                        AppController.user.editEmail(email)
                        AppController.user.
                        val intent =
                            Intent(context,MainActivity::class.java)
                        activity.startActivity(intent)
                        activity.finish()

                    } else {
                        // If sign in fails, display a message t@o the user.
                        Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            context,
                            "Không thể đăng nhập",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }

        @JvmStatic
        fun signUp(activity: Activity, context: Context, username: String, password: String){
            Firebase.auth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        //Log.d(TAG, "createUserWithEmail:success")
                        Toast.makeText(
                            context,
                            "Tạo tài khoản thành công",
                            Toast.LENGTH_SHORT,
                        ).show()

                        //update lên firebase
                        val userData = hashMapOf(
                            "email" to username
                        )
                        val accountFirebase = db.collection("users").document(Firebase.auth.currentUser!!.uid).set(userData)

                        val intent =
                            Intent(context,MainActivity::class.java)
                        activity.startActivity(intent)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            context,
                            "Không thể tạo tài khoản",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }

        @JvmStatic
        fun signOut(context: Context){
            Firebase.auth.signOut() //đăng xuất

            Toast.makeText(
                context,
                "Đã đăng xuất thành công",
                Toast.LENGTH_SHORT,
            ).show()


        }

        @JvmStatic
        fun changePassword(context: Context, newPassword: String){
            val user = Firebase.auth.currentUser

            user!!.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            context,
                            "Đã đổi mật khẩu thành công",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }


    }
}