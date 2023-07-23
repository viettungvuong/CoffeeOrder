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
import com.tung.coffeeorder.AppController.Companion.db
import com.tung.coffeeorder.AppController.Companion.listCoffee
import com.tung.coffeeorder.AppController.Companion.sharedPreferences
import java.io.*
import java.util.LinkedList

class Cart private constructor(){ //private constructor để không cho gọi constructor để singleton
    companion object{
        @JvmStatic
        var singleton= Cart() //singleton
    }

    private val cartList=ArrayList<CoffeeInCart>() //giỏ hàng

    fun addToCart(coffeeInCart: CoffeeInCart){
        cartList.add(coffeeInCart)

        val tempList = LinkedList<String>()

        for (coffeeInCart in cartList){
            val desc=coffeeInCart.getName()+","+coffeeInCart.getSize().toString()+","+coffeeInCart.getQuantity().toString()
            tempList.add(desc)
        }

        //update cart
        //nếu đang dùng tài khoản onl
        if (sharedPreferences.getBoolean("online_acc",false)){
            updateToFirebase(tempList) //up lên firebase
        }
        else{
            updateLocally(tempList) //xuất ra file
        }
    }

    fun removeFromCart(index: Int){
        cartList.removeAt(index)
    }

    fun getList(): ArrayList<CoffeeInCart>{
        return this.cartList
    }

    private fun updateToFirebase(tempList: LinkedList<String>){
        val data = arrayListOf(
            tempList,
        )
        val createField = hashMapOf(
            "cart" to data //tạo field cho cart (array field)
        )

        //lấy collection favorite từ database
        //để add vào sau này
        val getFavorites =  db.collection("cart")
            .document(Firebase.auth.currentUser!!.uid)

        getFavorites //lấy document trên firebase
            .get()
            .addOnCompleteListener(OnCompleteListener {
                    task->if (task.isSuccessful()) {
                val document=task.result
                if (document!=null){ //có document
                    if (document.exists()){
                        //nếu có field products rồi
                        if (document.contains("cart")){
                            //nếu có field Products
                            getFavorites.update("cart", FieldValue.arrayUnion(tempList))
                        }
                        else{
                            getFavorites.set(createField)
                            //nếu không có field cart
                        }
                    }
                    else{
                        getFavorites.set(createField)
                        //nếu không có document
                    }
                }
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

            writer.close()
        } catch (e: Exception) {
            Log.d("Error","Không thể xuất ra file")
            return
        }
    }

    private fun fetchFromFirebase(){
        //lấy từ collection Favorites
        val getFavorites = db.collection("cart")
            .document(Firebase.auth.currentUser!!.uid)

        getFavorites.get()
            .addOnSuccessListener {
                    documentSnapshot->
                val cart = documentSnapshot.get("cart") as ArrayList<String>
                for (cartDesc in cart){
                    val split = cartDesc.split(',') //tách từ theo dấu phẩy
                    val temp= listCoffee
                    temp.sortedBy { it.getName() }
                    val tempCoffee = temp[listCoffee.binarySearch(split[0],{ obj1, obj2 ->
                        (obj1 as Coffee).getName().compareTo((obj2 as Coffee).getName())
                    })] //tìm object cà phê tương ứng
                    val coffeeInCart = CoffeeInCart(tempCoffee)
                    coffeeInCart.changeQuantity(split[2].toInt())
                    coffeeInCart.changeSize(split[1].toInt())
                    cartList.add(coffeeInCart)
                }
            }
    }

    private fun fetchLocally(){
        val file = File("cart")
        if (!file.exists()) {
            Log.d("Error", "Không có file")
            return
        }

        val stringBuilder = StringBuilder()
        try {
            val reader = BufferedReader(FileReader(file))

            var line: String
            while (reader.readLine().also { line = it } != null) {
                val cartDesc = line //với từng dòng
                val split = cartDesc.split(',') //tách từ theo dấu phẩy
                val temp= listCoffee
                temp.sortedBy { it.getName() }
                val tempCoffee = temp[listCoffee.binarySearch(split[0],{ obj1, obj2 ->
                    (obj1 as Coffee).getName().compareTo((obj2 as Coffee).getName())
                })] //tìm object cà phê tương ứng
                val coffeeInCart = CoffeeInCart(tempCoffee)
                coffeeInCart.changeQuantity(split[2].toInt())
                coffeeInCart.changeSize(split[1].toInt())
                cartList.add(coffeeInCart)
            }

            reader.close()
        } catch (e: Exception) {
            Log.d("Error", "Không thể đọc file file")
            return
        }
    }

    //đọc cart đã lưu
    fun fetch(){
        //nếu có tài khoản online
        if (sharedPreferences.getBoolean("online_acc",false)){
            fetchFromFirebase()
        }
        else{
            fetchLocally()
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
        lateinit var sharedPreferences: SharedPreferences //shared preferences
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
                        Log.d("Coffee size",coffeeInCart.getSize().toString())
                        Log.d("Cart size",temp[i].getSize().toString())
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
                        // Sign in success, update UI with the signed-in User.singleton's information
                        //Log.d(TAG, "signInWithEmail:success")
                        Toast.makeText(
                            context,
                            "Đã đăng nhập thành công",
                            Toast.LENGTH_SHORT,
                        ).show()

                        AppController.sharedPreferences.edit().putBoolean("online_acc",true) //ghi nhận là dùng tài khoản online cho app
                        AppController.sharedPreferences.edit().apply()


                        val email = task.result?.user?.email.toString()

                        getInfoFromFirebase(User.singleton
                        ) { id,name,phoneNumber,address ->
                            User.singleton.edit(name, email, phoneNumber, address,id)
                            val intent =
                                Intent(context, MainActivity::class.java)
                            activity.startActivity(intent)
                            activity.finish()
                        }


                    } else {
                        // If sign in fails, display a message t@o the User.singleton.
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

                        AppController.sharedPreferences.edit().putBoolean("online_acc",true) //ghi nhận là dùng tài khoản online cho app
                        AppController.sharedPreferences.edit().apply()

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

            AppController.sharedPreferences.edit().putBoolean("online_acc",false)
            AppController.sharedPreferences.edit().apply()

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