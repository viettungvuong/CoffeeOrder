package com.tung.coffeeorder

import android.content.Context
import android.media.Image
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.tung.coffeeorder.AppController.Companion.carts
import com.tung.coffeeorder.AppController.Companion.db
import com.tung.coffeeorder.AppController.Companion.historyOrders
import com.tung.coffeeorder.AppController.Companion.listCoffee
import com.tung.coffeeorder.AppController.Companion.numberOfCarts
import com.tung.coffeeorder.AppController.Companion.numberOfOrders
import com.tung.coffeeorder.AppController.Companion.ongoingOrders
import com.tung.coffeeorder.AppController.Companion.redeemCoffees
import com.tung.coffeeorder.AppController.Companion.rewardsPoint
import com.tung.coffeeorder.AppController.Companion.sharedPreferences
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.coroutines.suspendCoroutine

class Functions {
    companion object{

        @JvmStatic
        //reformat định dạng số
        fun reformatNumber(money: Long): String {
            if (money <= 100)
                return money.toString()


            var moneyString = money.toString();

            val strings = ArrayList<String>()

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

        fun initCoffeeList(listCoffee: LinkedList<Coffee>){
            listCoffee.add(Coffee("Cà phê sữa đá","caphesuada",18000))
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
                    if (date>LocalDateTime.now()){
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
                    redeemCoffees.add(redeemCoffee)
                }
            }
        }

        fun retrieveCurrentNoOfCarts(){
            if (!sharedPreferences.getBoolean("online_acc", false)) {
                numberOfCarts=sharedPreferences.getInt("number-of-carts", 0) //tăng số lượng cart lên
            }
            else{

                db.collection("users").document(Firebase.auth.currentUser!!.uid).get()
                    .addOnSuccessListener {
                        document->
                        numberOfCarts=(document.getLong("number-of-carts")?:0L).toInt()
                    }
            }
        }

        fun getCurrentNoOfCarts(): Int{
            return numberOfCarts //trả về số lượng giỏ hàng cho tới hiện tại
        }

        //cái này sẽ gọi khi checkout cart, cho nên là khi cart vẫn còn dang dở thì nó sẽ kh được gọi
        fun increaseCarts(){
            Log.d("number of carts", numberOfCarts.toString())
            Log.d("called", numberOfCarts.toString())
            numberOfCarts++
            //acc offline
            if (!sharedPreferences.getBoolean("online_acc", false)) {
                sharedPreferences.edit().putInt("number-of-carts", numberOfCarts)
                    .apply() //tăng số lượng cart lên
            }
            else{
                Log.d("number of carts", numberOfCarts.toString())
                val setField=mapOf(
                    "number-of-carts" to numberOfCarts
                )
                db.collection("users").document(Firebase.auth.currentUser!!.uid).set(setField, SetOptions.merge())
            }
        }

        fun retrieveCurrentNoOfOrders(){
            if (!sharedPreferences.getBoolean("online_acc", false)) {
                numberOfOrders=sharedPreferences.getInt("number-of-orders", 0) //tăng số lượng cart lên
            }
            else{

                db.collection("users").document(Firebase.auth.currentUser!!.uid).get()
                    .addOnSuccessListener {
                            document->
                        numberOfOrders=(document.getLong("number-of-orders")?:0L).toInt()
                    }
            }
        }

        fun getCurrentNoOfOrders(): Int{
            return numberOfOrders //trả về số lượng giỏ hàng cho tới hiện tại
        }

        //cái này sẽ gọi khi checkout cart, cho nên là khi cart vẫn còn dang dở thì nó sẽ kh được gọi
        fun increaseOrders(){
            numberOfOrders++
            //acc offline
            if (!sharedPreferences.getBoolean("online_acc", false)) {
                sharedPreferences.edit().putInt("number-of-orders", numberOfOrders)
                    .apply() //tăng số lượng cart lên
            }
            else{
                val setField=mapOf(
                    "number-of-orders" to numberOfOrders
                )
                db.collection("users").document(Firebase.auth.currentUser!!.uid).set(setField, SetOptions.merge())
            }
        }

        fun initCarts(){
            if (sharedPreferences.getBoolean("online_acc",false)){
                initCartsFromFirebase {
                    fetchOrders()
                }
            }
            else{
                initCartsLocally()
                fetchOrders() //lấy tất cả order (phải có cart thì mới lấy order được)
            }
        }

        //load tất cả các cart
        private fun initCartsFromFirebase(callback: ()->Unit){
            val getCart = db.collection("cart" + Firebase.auth.currentUser!!.uid)

            getCart.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.d("cart name",document.id)
                        val cart = document.get("cart") as ArrayList<String> //array field cart
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
                            currentCart.addToCart(coffeeInCart)
                        }
                        carts.add(currentCart) //thêm vào danh sách các cart
                    }

                    resumeCart()
                    callback()
                }

        }

        private fun initCartsLocally() {
            val file = File("carts")
            if (!file.exists()) {
                Log.d("Error", "Không có file")
                return
            }

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
                        currentCart.addToCart(coffeeInCart)
                    } else {
                        val temp = currentCart
                        carts.add(temp)
                        currentCart = Cart() //xoá cart hiện tại
                    }
                }

                resumeCart()
            } catch (e: Exception) {
                Log.d("Error", "Không thể đọc file")
                return
            }


        }

        fun fetchOrders(){
            if (sharedPreferences.getBoolean("online_acc",false)){
                fetchOrderFromFirebase() //lấy từ firebase
            }
            else{
                fetchOrderLocally() //đọc từ file
            }
        }

        private fun fetchOrderFromFirebase(){
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

                        ongoingOrders.add(currentOrder) //cứ để vào history order, nếu nó done thì gọi setDone nó sẽ loại khỏi ongoingOrders

                        if (done){
                            Log.d("setDone","setDone")
                            currentOrder.setDone(ongoingOrders, historyOrders, rewardsPoint)

                        }

                    }

                }
        }

        private fun fetchOrderLocally(){
            val file = File("orders")
            if (!file.exists()) {
                Log.d("Error", "Không có file")
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
                    ongoingOrders.add(currentOrder) //cứ để vào history order, nếu nó done thì gọi setDone nó sẽ loại khỏi ongoingOrders

                    if (done){
                        Log.d("setDone","setDone")
                        currentOrder.setDone(ongoingOrders, historyOrders, rewardsPoint)
                    }

                    index++
                }
            } catch (e: Exception) {
                Log.d("Error", "Không thể đọc file")
                return
            }
        }

        //resume cart
        fun resumeCart(){
            if (!needToResume()){
                Log.d("needtoresume", needToResume().toString())
                return
            }
            if (carts.isEmpty()){
                return
            }

            val resumeCart = carts[getCurrentNoOfCarts()-1].getList()
            for (item in resumeCart){
                Cart.singleton.addToCart(item) //dùng addToCart sẽ làm tăng số number-of-carts, dẫn đến kết quả sai
            }
        }

        fun needToResume(): Boolean{
            return getCurrentNoOfCarts() > getCurrentNoOfOrders()
        }


    }

}

