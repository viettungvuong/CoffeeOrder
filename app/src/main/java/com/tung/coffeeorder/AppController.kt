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
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.tung.coffeeorder.AppController.Companion.getCurrentNoOfCarts
import com.tung.coffeeorder.AppController.Companion.carts
import com.tung.coffeeorder.AppController.Companion.db
import com.tung.coffeeorder.AppController.Companion.initCarts
import com.tung.coffeeorder.AppController.Companion.listCoffee
import com.tung.coffeeorder.AppController.Companion.retrieveCurrentNoOfCarts
import com.tung.coffeeorder.AppController.Companion.retrieveCurrentNoOfOrders
import com.tung.coffeeorder.AppController.Companion.sharedPreferences
import java.io.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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
        var listCoffee= ArrayList<Coffee>() //danh sách các coffee
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

        //lấy địa chỉ của User.singleton từ firebase
        @JvmStatic
        fun getInfoFromFirebase(user: User, callback: (String,String,String,String,Int)->Unit){
            db.collection("users").document(Firebase.auth.uid.toString()).get()
                .addOnSuccessListener {
                        documentSnapshot->
                    if (documentSnapshot.exists()){
                        val id =documentSnapshot.id
                        Log.d("Accountid",id)
                        val address=documentSnapshot.getString("address").toString()
                        val name =documentSnapshot.getString("name").toString() //để tìm hiểu cách chỉnh display name cho nó đúng
                        val phoneNumber=documentSnapshot.getString("phone-number").toString()
                        val loyaltyPoint=documentSnapshot.getLong("loyalty-points")!!.toInt()
                        callback(id,name,phoneNumber,address,loyaltyPoint)
                    }
                }
        }
        @JvmStatic
        //reformat định dạng số
        fun reformatNumber(money: Long): String {
            if (money <= 100)
                return money.toString()


            var moneyString = money.toString();

            val strings = java.util.ArrayList<String>()

            val n = moneyString.length - 1;

            for (i in n downTo 0 step 3) {
                val start = Integer.max(i - 2, 0)
                val end = Integer.min(n + 1, i + 1)
                val s = moneyString.substring(start, end)
                strings.add(s)
                strings.add(",")
            }

            if (strings[strings.size - 1] == ",") {
                strings.removeAt(strings.size- 1);
            }

            strings.reverse() //đảo ngược mảng

            moneyString = ""

            for (i in 0..strings.size- 1) {
                moneyString += strings[i]
            }

            return moneyString;
            //gio ta phai cho no xuat dung chieu
        }

        fun initCoffeeList(listCoffee: ArrayList<Coffee>){
            listCoffee.add(Coffee("Cà phê sữa","caphesuada",18000))
            listCoffee.add(Coffee("Cà phê muối","caphemuoi",19000))
            listCoffee.add(Coffee("Americano","americano",35000))
            listCoffee.add(Coffee("Cappuccino","cappuccino",36000))
            listCoffee.add(Coffee("Espresso","espresso",33000))
            listCoffee.add(Coffee("Cold brew","coldbrew",42000))
            listCoffee.add(Coffee("Bạc sỉu","bacsiu",20000))
            listCoffee.add(Coffee("Latte","latte",41000))
        }

        fun imageFromCoffee(context: Context, coffee: Coffee): Int {
            return context.resources.getIdentifier(coffee.getImageFilename(),
                "drawable",
                context.packageName)
        }

        fun initRedeem() {
            db.collection("redeem").get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val date = LocalDateTime.parse(document.getString("valid-date"), DateTimeFormatter.ofPattern(
                        AppController.dateFormat
                    ))
                    if (date> LocalDateTime.now()){
                        continue //đã quá invalid date nên không thêm nữa
                    }
                    val coffeeName = document.id
                    val temp = AppController.listCoffee
                    temp.sortedBy { it.getName() }
                    val tempCoffee = listCoffee.find { coffee ->
                        coffee.getName() == coffeeName
                    }//tìm object cà phê tương ứng
                    val size = document.getLong("size")?.toInt()
                    val points = document.getLong("points")?.toInt()
                    val redeemCoffee=RedeemCoffee(tempCoffee!!,date,size!!,points!!)
                    AppController.redeemCoffees.add(redeemCoffee)
                }
            }
        }

        fun retrieveCurrentNoOfCarts(){
            if (!sharedPreferences.getBoolean("online_acc", false)) {
                AppController.numberOfCarts =sharedPreferences.getInt("number-of-carts", 0) //tăng số lượng cart lên
            }
            else{

                db.collection("users").document(Firebase.auth.currentUser!!.uid).get()
                    .addOnSuccessListener {
                            document->
                        AppController.numberOfCarts =(document.getLong("number-of-carts")?:0L).toInt()
                    }
            }
        }

        fun getCurrentNoOfCarts(): Int{
            return AppController.numberOfCarts //trả về số lượng giỏ hàng cho tới hiện tại
        }

        //cái này sẽ gọi khi checkout cart, cho nên là khi cart vẫn còn dang dở thì nó sẽ kh được gọi
        fun increaseCarts(){
            AppController.numberOfCarts++
            //acc offline
            if (!sharedPreferences.getBoolean("online_acc", false)) {
                sharedPreferences.edit().putInt("number-of-carts", AppController.numberOfCarts)
                    .apply() //tăng số lượng cart lên
            }
            else{
                Log.d("number of carts", AppController.numberOfCarts.toString())
                val setField=mapOf(
                    "number-of-carts" to AppController.numberOfCarts
                )
                db.collection("users").document(Firebase.auth.currentUser!!.uid).set(setField, SetOptions.merge())
            }
        }

        fun retrieveCurrentNoOfOrders(){
            if (!sharedPreferences.getBoolean("online_acc", false)) {
                AppController.numberOfOrders =sharedPreferences.getInt("number-of-orders", 0) //tăng số lượng cart lên
            }
            else{

                db.collection("users").document(Firebase.auth.currentUser!!.uid).get()
                    .addOnSuccessListener {
                            document->
                        AppController.numberOfOrders =(document.getLong("number-of-orders")?:0L).toInt()
                    }
            }
        }

        fun getCurrentNoOfOrders(): Int{
            return AppController.numberOfOrders //trả về số lượng giỏ hàng cho tới hiện tại
        }

        //cái này sẽ gọi khi checkout cart, cho nên là khi cart vẫn còn dang dở thì nó sẽ kh được gọi
        fun increaseOrders(){
            AppController.numberOfOrders++
            //acc offline
            if (!sharedPreferences.getBoolean("online_acc", false)) {
                sharedPreferences.edit().putInt("number-of-orders", AppController.numberOfOrders)
                    .apply() //tăng số lượng cart lên
            }
            else{
                val setField=mapOf(
                    "number-of-orders" to AppController.numberOfOrders
                )
                db.collection("users").document(Firebase.auth.currentUser!!.uid).set(setField, SetOptions.merge())
            }
        }

        @JvmStatic
        fun initCarts(context: Context){
            if (sharedPreferences.getBoolean("online_acc",false)){
                initCartsFromFirebase(context) {
                    fetchOrders(context)
                }
            }
            else{
                initCartsLocally(context)
                fetchOrders(context) //lấy tất cả order (phải có cart thì mới lấy order được)
            }
        }

        //load tất cả các cart
        private fun initCartsFromFirebase(context: Context, callback: ()->Unit){
            val getCart = db.collection("cart" + Firebase.auth.currentUser!!.uid)

            getCart.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        if (document.id=="0"){
                            continue //bỏ qua thằng 0
                            //để kiếm cách fix thằng 0 này sau
                        }
                        val cart = document.get("cart") as java.util.ArrayList<String> //array field cart
                        var currentCart=Cart()
                        for (cartDesc in cart) {
                            val split = cartDesc.split(',') //tách từ theo dấu phẩy

                            //tìm cà phê
                            val temp = listCoffee
                            temp.sortedBy { it.getName() }
                            val tempCoffee = listCoffee.find { coffee ->
                                coffee.getName() == split[0]
                            }//tìm object cà phê tương ứng
                            val coffeeInCart = CoffeeInCart(tempCoffee!!)
                            coffeeInCart.changeQuantity(split[2].toInt())
                            coffeeInCart.changeSize(split[1].toInt())
                            currentCart.addToCart(context,coffeeInCart)
                        }
                        carts.add(currentCart) //thêm vào danh sách các cart
                    }

                    resumeCart(context)
                    callback()
                }

        }

        private fun initCartsLocally(context: Context) {
            val file = File(context.filesDir, cartsFileName)
            if (!file.exists()) {
                Log.d("Error", "Không có file cart")
                return
            }
            Log.d("Đọc file", "Có file")
            val lines = file.readLines()

            var currentCart = Cart()

            try {
                for (line in lines) {
                    if (line.isNotBlank()) {
                        val split = line.split(',') //tách từ theo dấu phẩy

                        //tìm cà phê
                        val temp = AppController.listCoffee
                        temp.sortedBy { it.getName() }
                        val tempCoffee = listCoffee.find { coffee ->
                            coffee.getName() == split[0]
                        }//tìm object cà phê tương ứng //tìm object cà phê tương ứng
                        val coffeeInCart = CoffeeInCart(tempCoffee!!)
                        coffeeInCart.changeQuantity(split[2].toInt())
                        coffeeInCart.changeSize(split[1].toInt())
                        Log.d("split[0]",split[0])
                        Log.d("split[1]",split[1])
                        Log.d("split[2]",split[2])
                        currentCart.addToCart(context,coffeeInCart)
                    } else {
                        val temp = Cart(currentCart) //copy constructor
                        carts.add(temp)
                        currentCart = Cart() //xoá cart hiện tại
                    }
                }
                val temp = Cart(currentCart) //copy constructor
                carts.add(temp) //add thêm một lần nữa ở cuối file

                resumeCart(context)
                Log.d("cart",carts.size.toString())
                Log.d("current cart",Cart.singleton.getList().size.toString())
            } catch (e: Exception) {
                Log.d("Error", "Không thể đọc file carts")
                return
            }


        }

        fun fetchOrders(context: Context){
            if (sharedPreferences.getBoolean("online_acc",false)){
                fetchOrderFromFirebase(context) //lấy từ firebase
            }
            else{
                fetchOrderLocally(context) //đọc từ file
            }
        }

        private fun fetchOrderFromFirebase(context: Context){
            val getOrder = AppController.db.collection("orders"+Firebase.auth.currentUser!!.uid)

            getOrder.get()
                .addOnSuccessListener {
                        documents->
                    for (document in documents){
                        val time = LocalDateTime.parse(document.getString("time"), DateTimeFormatter.ofPattern(
                            AppController.dateFormat
                        ))
                        val id = document.id
                        val address = document.getString("address")
                        var done = false
                        done = document.getString("done")=="true"

                        val currentOrder=Order(carts[document.id.toInt()-1].getList(),time,address!!,id.toInt())

                        AppController.ongoingOrders.add(currentOrder) //cứ để vào history order, nếu nó done thì gọi setDone nó sẽ loại khỏi ongoingOrders

                        if (done){
                            currentOrder.setDone(
                                AppController.ongoingOrders,
                                AppController.historyOrders,
                                AppController.rewardsPoint, context,true)

                        }

                    }

                }
        }

        private fun fetchOrderLocally(context: Context){
            val file = File(context.filesDir, orderFileName)
            if (!file.exists()) {
                Log.d("Error", "Không có file order")
                return
            }

            val lines = file.readLines()

            var index = 0
            try {
                for (line in lines) {
                    val lineSplit = line.split(',')
                    val id = lineSplit[0]
                    val time = LocalDateTime.parse(lineSplit[1], DateTimeFormatter.ofPattern(
                        AppController.dateFormat
                    ))
                    val address = lineSplit[2]
                    var done = false
                    done = (lineSplit[3]=="true")

                    val currentOrder=Order(carts[index].getList(),time,address!!,id.toInt())
                    AppController.ongoingOrders.add(currentOrder) //cứ để vào history order, nếu nó done thì gọi setDone nó sẽ loại khỏi ongoingOrders

                    if (done){
                        Log.d("setDone","setDone")
                        currentOrder.setDone(
                            AppController.ongoingOrders,
                            AppController.historyOrders,
                            AppController.rewardsPoint, context, true)
                    }

                    index++
                }
            } catch (e: Exception) {
                Log.d("Error","Không thể đọc file order"+e.message.toString())
                return
            }
        }

        //resume cart
        fun resumeCart(context: Context){
            if (!needToResume()){
                Log.d("needtoresume", needToResume().toString())
                return
            }
            if (carts.isEmpty()){
                return
            }

            val resumeCart = carts[getCurrentNoOfCarts()-1].getList()
            for (item in resumeCart){
                Cart.singleton.addToCart(context,item)
            }
        }

        //có cần phải resumecart kh
        fun needToResume(): Boolean{
            return getCurrentNoOfCarts() > getCurrentNoOfOrders()
        }




    }


}

class AccountFunctions {
    companion object{
        fun logout(context: Context){
            AccountFunctions.signOut(context)
            sharedPreferences.edit().putBoolean("online_acc",true).apply()
            carts.clear()
            Cart.singleton.getList().clear()
            AppController.ongoingOrders.clear()
            AppController.historyOrders.clear()
            AppController.rewardsPoint.clear()
            User.singleton.clearUser()
            AppController.numberOfCarts = 0 //số cart (kể cả cart chưa hoàn thành)
            AppController.numberOfOrders = 0 //số order
        }
        @JvmStatic
        fun signIn(activity: Activity, context: Context, username: String, password: String){
            Firebase.auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        val intent =
                            Intent(activity, MainActivity::class.java)

                        sharedPreferences.edit()
                            .putBoolean("online_acc", true)
                            .apply() //ghi nhận là dùng tài khoản online cho app

                        Toast.makeText(
                            activity,
                            "Đã đăng nhập thành công",
                            Toast.LENGTH_SHORT,
                        ).show()

                        val email = Firebase.auth.currentUser!!.email.toString()

                        AppController.getInfoFromFirebase(
                            User.singleton
                        ) { id, name, phoneNumber, address,loyaltyPoint ->
                            User.singleton.initialize(
                                Firebase.auth.currentUser!!.uid,
                                name,
                                email,
                                phoneNumber,
                                address,
                                loyaltyPoint
                            )
                            initCarts(activity) //lấy danh sách các cart
                            retrieveCurrentNoOfCarts()
                            retrieveCurrentNoOfOrders()
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


    }
}