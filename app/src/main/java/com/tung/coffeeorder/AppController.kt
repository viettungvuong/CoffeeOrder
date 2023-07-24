package com.tung.coffeeorder

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Address
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.tung.coffeeorder.AppController.Companion.carts
import com.tung.coffeeorder.AppController.Companion.db
import com.tung.coffeeorder.AppController.Companion.listCoffee
import com.tung.coffeeorder.AppController.Companion.sharedPreferences
import com.tung.coffeeorder.Functions.Companion.getCurrentNoOfCarts
import com.tung.coffeeorder.Functions.Companion.increaseCarts
import java.io.*
import java.util.LinkedList

const val orderFileName = "orders-save-app.dat"
const val cartsFileName = "carts-save-app.dat"
class Cart() {

    private var cartList=ArrayList<CoffeeInCart>() //giỏ hàng của cart
    companion object{
        @JvmStatic
        var singleton= Cart() //singleton
    }

    constructor(otherCart: Cart): this(){
        cartList=otherCart.cartList
    }


    private fun getDescList(): LinkedList<String>{ //mảng chứa mô tả các sản phẩm trong cart
        val tempList = LinkedList<String>()

        for (coffeeInCart in cartList){
            val desc=coffeeInCart.getName()+","+coffeeInCart.getSize().toString()+","+coffeeInCart.getquantity().toString()
            tempList.add(desc)
        }

        return tempList
    }


    fun addToCart(context: Context,coffeeInCart: CoffeeInCart){
        cartList.add(coffeeInCart)
        update(context)
    }

    fun removeFromCart(context: Context, index: Int){
        cartList.removeAt(index)
        update(context)
    }

    fun getList(): ArrayList<CoffeeInCart>{
        return this.cartList
    }

    fun update(context: Context){
        if (sharedPreferences.getBoolean("online_acc",false)){
            Log.d("Updated firebase","ok")
            updateToFirebase(getDescList()) //up lên firebase
        }
        else{
            Log.d("Updated locally","ok")
            updateLocally(context,getDescList()) //xuất ra file
        }
    }

    private fun updateToFirebase(tempList: LinkedList<String>){

        val createField = hashMapOf(
            "cart" to tempList //tạo field cho cart (array field)
        )


        val getCart = db.collection("cart" + Firebase.auth.currentUser!!.uid)
           .document(getCurrentNoOfCarts().toString())

        val deleteField = mapOf(
            "cart" to FieldValue.delete()
        )

        getCart.update(deleteField)

        getCart
            .get()
            .addOnCompleteListener(OnCompleteListener {
                    task->if (task.isSuccessful()) {
                getCart.set(createField) //thêm cart
            }
            })
    }

    private fun updateLocally(context: Context, tempList: LinkedList<String>){
        val file = File(context.filesDir, cartsFileName)
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: IOException) {
                Log.d("Error",e.message.toString())
                return
            }
        }

        try {
            val lines =  file.readLines().toMutableList() //đọc toàn bộ dòng và lưu vào một mảng

            var i = lines.size - 1
            while (i >= 0 && lines[i].isNotBlank()) { //đọc đến khi gặp dòng trống
                lines.removeAt(i) //xoá dòng
                i--
            }

            lines.addAll(tempList)

            //xuất từng dòng ra file, dùng jointostring với kí tự '\n' để xuống dòng
            print("Update locally")
            print(lines)
            file.writeText(lines.joinToString("\n"))

        } catch (e: Exception) {
            Log.d("Error","Không thể xuất ra file")
            return
        }
    }

}

class AppController{
    companion object{
        @JvmStatic
        val dateFormat = "dd-MM-yyyy HH:mm" //format ngày tháng
        val ongoingOrders=LinkedList<Order>() //danh sách các order onging
        val historyOrders=LinkedList<Order>() //danh sách các order history
        val rewardsPoint=LinkedList<Reward>() //danh sách điểm thưởng
        val redeemCoffees= LinkedList<RedeemCoffee>()
        lateinit var ongoingAdapter: OrderAdapter
        lateinit var historyAdapter: OrderAdapter //để 2 adapter này ở đây vì hai adapter này có sự liên thông với nhau rất nhiều
        var db= Firebase.firestore
        val storage = Firebase.storage.reference
        var listCoffee= LinkedList<Coffee>() //danh sách các coffee
        lateinit var sharedPreferences: SharedPreferences //shared preferences
        var carts= ArrayList<Cart>() //danh sách các cart
        var numberOfCarts = 0 //số cart (kể cả cart chưa hoàn thành)
        var numberOfOrders = 0 //số order


        @JvmStatic
        fun checkInCart(coffeeInCart: CoffeeInCart): Int{
            val temp = Cart.singleton.getList().toList().sortedBy { it.getName() } //sort cart theo tên

            var i=0
            while (i<temp.size&&coffeeInCart.getName()<=temp[i].getName()){
                if (coffeeInCart.getName()==temp[i].getName()&&coffeeInCart.getSize()==temp[i].getSize()){
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
                        val intent =
                            Intent(activity, MainActivity::class.java)


                        Toast.makeText(
                            context,
                            "Đã đăng nhập thành công",
                            Toast.LENGTH_SHORT,
                        ).show()

                        sharedPreferences.edit().putBoolean("online_acc",true).apply() //ghi nhận là dùng tài khoản online cho app


                        val email = task.result?.user?.email.toString()

                       getInfoFromFirebase(
                            User.singleton
                        ) { id, name, phoneNumber, address ->
                            Log.d("Accountid2", id)
                            User.singleton.initialize(Firebase.auth.currentUser!!.uid, name, email, phoneNumber, address)
                            Functions.initCarts(context) //lấy danh sách các cart
                            Functions.retrieveCurrentNoOfCarts()
                            Functions.retrieveCurrentNoOfOrders()
                            activity.startActivity(intent)
                            activity.finish()
                        }

                    } else {
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
        fun signUp(activity: Activity, context: Context, username: String, password: String, name: String, phoneNumber: String, address: String){
            Firebase.auth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            context,
                            "Tạo tài khoản thành công",
                            Toast.LENGTH_SHORT,
                        ).show()

                        sharedPreferences.edit().putBoolean("online_acc",true).apply() //ghi nhận là dùng tài khoản online cho app

                        //đặt vào người dùng hiện tại
                        User.singleton.edit(Firebase.auth.currentUser!!.uid,name,username,phoneNumber,address,true)

                        val intent =
                            Intent(context,MainActivity::class.java)
                        activity.startActivity(intent)

                    } else {
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

            sharedPreferences.edit().putBoolean("online_acc",false).apply()

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

        //lấy địa chỉ của User.singleton từ firebase
        @JvmStatic
        fun getInfoFromFirebase(user: User, callback: (String,String,String,String)->Unit){
            db.collection("users").document(Firebase.auth.uid.toString()).get()
                .addOnSuccessListener {
                    documentSnapshot->
                    if (documentSnapshot.exists()){
                        val id =documentSnapshot.id
                        Log.d("Accountid",id)
                        val address=documentSnapshot.getString("address").toString()
                        val name =documentSnapshot.getString("name").toString() //để tìm hiểu cách chỉnh display name cho nó đúng
                        val phoneNumber=documentSnapshot.getString("phone-number").toString()
                        callback(id,name,phoneNumber,address)
                    }
                }
        }


    }
}