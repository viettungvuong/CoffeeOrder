package com.tung.coffeeorder

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.tung.coffeeorder.AppController.Companion.initCarts
import com.tung.coffeeorder.AppController.Companion.resetAll
import com.tung.coffeeorder.AppController.Companion.retrieveNoCartsOrders
import com.tung.coffeeorder.AppController.Companion.sharedPreferences
import com.tung.coffeeorder.adapters.OrderAdapter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.LinkedList

class AppController{
    companion object{
        @JvmStatic
        val dateFormat= DateTimeFormatter.ofPattern("dd-MM-yyyy") //format ngày tháng
        val dateTimeFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm") //format ngày tháng giờ
        val timeFormat = DateTimeFormatter.ofPattern("HH:mm")

        val ongoingOrders=LinkedList<Order>() //danh sách các order onging
        val historyOrders=LinkedList<Order>() //danh sách các order history
        val rewardsPoint=LinkedList<Reward>() //danh sách điểm thưởng

        val redeemCoffees= LinkedList<RedeemCoffee>()
        
        lateinit var ongoingAdapter: OrderAdapter
        lateinit var historyAdapter: OrderAdapter //để 2 adapter này ở đây vì hai adapter này có sự liên thông với nhau rất nhiều

        var db= Firebase.firestore

        var listCoffee= ArrayList<Coffee>() //danh sách các coffee

        lateinit var sharedPreferences: SharedPreferences //shared preferences

        var currentCart=Cart(0, ArrayList())//cart chính của chương trình
        //sẽ initialize sau

        var carts= ArrayList<Cart>() //danh sách các cart
        var numberOfCarts = 0 //số cart (kể cả cart chưa hoàn thành)
        var numberOfOrders = 0 //số order
        var numberOfRedeem=0 //số order redeem


        @JvmStatic
        fun checkInCart(coffeeInCart: CoffeeInCart): Int{
            val temp = currentCart!!.cartList.toList().sortedBy { it.getName() } //sort cart theo tên

            var i=0
            while (i<temp.size&&coffeeInCart.getName()<=temp[i].getName()){
                if (coffeeInCart.getName()==temp[i].getName()&&coffeeInCart.getSize()==temp[i].getSize()&&coffeeInCart.getShot()==temp[i].getShot()
                    &&coffeeInCart.getHotOrCold()==temp[i].getHotOrCold()){
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

            listCoffee.sortedBy { it.getName() } //sort
        }

        fun imageFromCoffee(context: Context, coffee: Coffee): Int {
            return context.resources.getIdentifier(coffee.getImageFilename(),
                "drawable",
                context.packageName)
        }

        fun initRedeem() {
            Log.d("init redeem","Đang init redeem")
            db.collection("redeem").get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
                    val date = LocalDate.parse(document.getString("valid-date")!!, formatter)
                    if (date< LocalDate.now()){
                        continue //đã quá valid date nên không thêm nữa
                    }
                    val coffeeName = document.getString("coffee-name")
                    val temp = AppController.listCoffee
                    temp.sortedBy { it.getName() }
                    val tempCoffee = AppController.listCoffee.find { coffee ->
                        coffee.getName() == coffeeName
                    }//tìm object cà phê tương ứng
                    val size = document.getLong("size")?.toInt()
                    val points = document.getLong("points")?.toInt()
                    val redeemCoffee=RedeemCoffee(tempCoffee!!,date,size!!,points!!)
                    redeemCoffees.add(redeemCoffee)
                }
            }
        }


        fun increaseRedeems(){
            numberOfRedeem++
            //acc offline
            if (!sharedPreferences.getBoolean("online_acc", false)) {
                sharedPreferences.edit().putInt("number-of-redeems", AppController.numberOfCarts)
                    .apply() //tăng số lượng redeem lên
            }
        }


        //cái này sẽ gọi khi checkout cart, cho nên là khi cart vẫn còn dang dở thì nó sẽ kh được gọi
        fun increaseCarts(){
            numberOfCarts++

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

        //lấy số order (để resume)
        fun retrieveNoCartsOrders(callback: () -> Unit){
            if (!sharedPreferences.getBoolean("online_acc", false)) {
                numberOfCarts =sharedPreferences.getInt("number-of-carts", 0) //tăng số lượng cart lên
                numberOfOrders =sharedPreferences.getInt("number-of-orders", 0) //tăng số lượng cart lên
                callback()
            }
            else{

                db.collection("users").document(Firebase.auth.currentUser!!.uid).get()
                    .addOnSuccessListener {
                            document->
                        numberOfCarts =(document.getLong("number-of-carts")?:0L).toInt()

                        db.collection("users").document(Firebase.auth.currentUser!!.uid).get()
                            .addOnSuccessListener {
                                    document->
                                numberOfOrders =(document.getLong("number-of-orders")?:0L).toInt()

                                callback() //gọi callback
                            }
                    }



            }
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
                    resumeCart(context)
                    fetchOrders(context)

                }
            }
            else{
                initCartsLocally(context){
                    resumeCart(context)
                    fetchOrders(context) //lấy tất cả order (phải có cart thì mới lấy order được)
                }
            }
        }

        //load tất cả các cart
        private fun initCartsFromFirebase(context: Context, callback: ()->Unit){
            val getCart = db.collection("users").document(Firebase.auth.currentUser!!.uid)
                .collection("carts")

            getCart.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        if (document.id=="0"){
                            continue //bỏ qua thằng 0
                            //để kiếm cách fix thằng 0 này sau
                        }
                        val cart = document.get("cart") as java.util.ArrayList<String> //array field cart
                        var currentCart=Cart(document.id.toInt(),ArrayList())
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
                            coffeeInCart.changeHotOrCold((split[3]=="true"))
                            coffeeInCart.changeShot((split[4]=="true"))
                            addToCart(currentCart!!,context,coffeeInCart)
                        }
                        carts.add(currentCart!!) //thêm vào danh sách các cart
                    }

                    callback()
                }

        }

        private fun initCartsLocally(context: Context,callback: () -> Unit) {
            val list = AppDatabase.getSingleton(context).cartDao().getAllCarts()

            for (cart in list){
                carts.add(cart)
            }

            callback()

        }

        fun searchCoffeeByName(name: String): Coffee?{
            val tempCoffee = listCoffee.find { coffee ->
                coffee.getName() == name
            }//tìm object cà phê tương ứng //tìm object cà phê tương ứng
            return tempCoffee
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
            val getOrder = db.collection("users").document(Firebase.auth.currentUser!!.uid)
                .collection("orders")

            getOrder.get()
                .addOnSuccessListener {
                        documents->
                    for (document in documents){
                        val time = document.getString("time")
                        val id = document.id
                        val address = document.getString("address")
                        val done = document.getString("done")=="true"
                        val redeem = document.getString("redeem")=="true"

                        var currentOrder: Order?=null
                        if (!redeem){
                            currentOrder=Order(id.toInt(),address!!,time!!,carts[document.id.toInt()-1].cartList)
                            currentOrder.redeem=false
                        }
                        else{ //nếu là redeem
                            val coffeeName = document.getString("redeemCoffee")
                            val size=document.getLong("redeemSize")!!.toInt()
                            val redeemPoint = document.getLong("redeemPoint")!!.toInt()
                            val redeemCoffee= RedeemCoffee(searchCoffeeByName(coffeeName!!)!!,size,redeemPoint)
                            currentOrder=Order(id.toInt(),address!!,time!!,
                                ArrayList<CoffeeInCart>()
                            )
                            setRedeem(currentOrder,redeemCoffee.getRedeemPoints(),context,true) //setredeem từ init
                            currentOrder.cart.add(redeemCoffee)
                        }

                        ongoingOrders.add(currentOrder) //cứ để vào history order, nếu nó done thì gọi setDone nó sẽ loại khỏi ongoingOrders

                        if (done){
                            setOrderDone(currentOrder,
                                ongoingOrders,
                                historyOrders,
                                rewardsPoint, context,true)

                        }

                    }

                }
        }

        private fun fetchOrderLocally(context: Context){
                val list = AppDatabase.getSingleton(context).orderDao().getAllOrders()

                for (order in list){
                    Log.d("order",order.id.toString()+" "+order.redeem+" "+order.done)
                    ongoingOrders.add(order) //cứ để vào history order, nếu nó done thì gọi setDone nó sẽ loại khỏi ongoingOrders

                    if (order.done){
                        setOrderDone(order,
                            ongoingOrders,
                            historyOrders,
                            rewardsPoint, context,true)

                    }
                }

        }

        //resume cart
        private fun resumeCart(context: Context){

            if (!needToResume()){
                currentCart=Cart(numberOfCarts+1,ArrayList()) //tạo cart rỗng nếu không cần resume
                return
            }
            if (carts.isEmpty()){
                return
            }
            val resumeCart = carts[numberOfCarts-1].cartList
            currentCart=Cart(numberOfCarts, ArrayList())
            for (item in resumeCart){
                addToCart(currentCart!!,context,item)
            }
        }

        //có cần phải resumecart kh
        private fun needToResume(): Boolean{
            Log.d("no of carts", numberOfCarts.toString())
            Log.d("no of orders", numberOfOrders.toString())
            return numberOfCarts > numberOfOrders
        }

        fun resetAll(){
            carts.clear()
            redeemCoffees.clear() //xoá danh sách redeem
            currentCart!!.cartList.clear()
            ongoingOrders.clear()
            historyOrders.clear()
            rewardsPoint.clear()
            User.singleton.clearUser()
            numberOfCarts = 0 //số cart (kể cả cart chưa hoàn thành)
            numberOfOrders = 0 //số order
            numberOfRedeem=0
        }

    }


}

