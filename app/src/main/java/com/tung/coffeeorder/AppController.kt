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
import java.io.*
import java.util.LinkedList

class Cart {
    companion object{
        @JvmStatic
        var singleton= Cart() //singleton
    }

    private val cartList=ArrayList<CoffeeInCart>() //giỏ hàng của cart

    private fun getDescList(): LinkedList<String>{ //mảng chứa mô tả các sản phẩm trong cart
        val tempList = LinkedList<String>()

        for (coffeeInCart in cartList){
            val desc=coffeeInCart.getName()+","+coffeeInCart.getSize().toString()+","+coffeeInCart.getquantity().toString()
            tempList.add(desc)
        }

        return tempList
    }


    fun addToCart(coffeeInCart: CoffeeInCart){
        cartList.add(coffeeInCart)

        update()
    }

    fun removeFromCart(index: Int){
        cartList.removeAt(index)
    }

    fun getList(): ArrayList<CoffeeInCart>{
        return this.cartList
    }

    fun update(){
        if (sharedPreferences.getBoolean("online_acc",false)){
            Log.d("Updated firebase","ok")
            updateToFirebase(getDescList()) //up lên firebase
        }
        else{
            Log.d("Updated locally","ok")
            updateLocally(getDescList()) //xuất ra file
        }
    }

    private fun updateToFirebase(tempList: LinkedList<String>){

        val createField = hashMapOf(
            "cart" to arrayListOf(
                tempList,
            ) //tạo field cho cart (array field)
        )

        val deleteUpdates = mapOf(
            "cart" to FieldValue.delete()
        )
        //lấy collection favorite từ database
        //để add vào sau này
        val getCart = db.collection("cart" + Firebase.auth.currentUser!!.uid)
            .document(getCurrentNoOfCarts().toString())

        getCart.update(deleteUpdates) //xoá hết giá trị trong cart

        getCart
            .get()
            .addOnCompleteListener(OnCompleteListener {
                    task->if (task.isSuccessful()) {
                getCart.set(createField) //thêm cart
            }
            })
    }

    private fun updateLocally(tempList: LinkedList<String>){
        val file = File("cart")
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

            for (temp in tempList){
                writer.write(temp)
                writer.newLine()
            }

            writer.write("")

            writer.close()
        } catch (e: Exception) {
            Log.d("Error","Không thể xuất ra file")
            return
        }
    }


    //resume cart
    fun resume(){
        if (carts.isEmpty()){
            return
        }
        cartList.clear() //xoá toàn bộ giỏ hàng

        val resumeCart = carts[getCurrentNoOfCarts()].cartList
        for (item in resumeCart){
            addToCart(item)
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
        lateinit var ongoingAdapter: OrderAdapter
        lateinit var historyAdapter: OrderAdapter //để 2 adapter này ở đây vì hai adapter này có sự liên thông với nhau rất nhiều
        var db= Firebase.firestore
        val storage = Firebase.storage.reference
        var listCoffee= LinkedList<Coffee>() //danh sách các coffee
        lateinit var redeemCoffees: LinkedList<RedeemCoffee>
        lateinit var sharedPreferences: SharedPreferences //shared preferences
        var carts= ArrayList<Cart>() //danh sách các cart
//        val dbCoffeeList="coffee"
//        val dbCoffeeNameField="name"
//        val dbCoffeeImageField="imageName"
//        val dbCoffeePriceField="price"



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

                        Toast.makeText(
                            context,
                            "Đã đăng nhập thành công",
                            Toast.LENGTH_SHORT,
                        ).show()

                        sharedPreferences.edit().putBoolean("online_acc",true).apply() //ghi nhận là dùng tài khoản online cho app


                        val email = task.result?.user?.email.toString()

                        getInfoFromFirebase(User.singleton
                        ) { id,name,phoneNumber,address ->
                            User.singleton.initialize(id,name, email, phoneNumber, address)

                            val intent =
                                Intent(context, MainActivity::class.java)
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
                        val address=documentSnapshot.getString("address").toString()
                        val name =documentSnapshot.getString("name").toString() //để tìm hiểu cách chỉnh display name cho nó đúng
                        val phoneNumber=documentSnapshot.getString("phone-number").toString()
                        callback(id,name,phoneNumber,address)
                    }
                }
        }


    }
}