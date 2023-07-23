package com.tung.coffeeorder

import android.content.Context
import android.media.Image
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.tung.coffeeorder.AppController.Companion.carts
import com.tung.coffeeorder.AppController.Companion.db
import com.tung.coffeeorder.AppController.Companion.redeemCoffees
import com.tung.coffeeorder.AppController.Companion.sharedPreferences
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.time.LocalDateTime
import java.time.ZoneId
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
                    val date = LocalDateTime.ofInstant(document.getDate("valid-date")?.toInstant(), ZoneId.systemDefault())
                    if (date>LocalDateTime.now()){
                        continue //đã quá invalid date nên không thêm nữa
                    }
                    val coffeeName = document.id
                    val temp = AppController.listCoffee
                    temp.sortedBy { it.getName() }
                    val tempCoffee =
                        temp[AppController.listCoffee.binarySearch(coffeeName, { obj1, obj2 ->
                            (obj1 as Coffee).getName().compareTo((obj2 as Coffee).getName())
                        })] //tìm object cà phê tương ứng
                    val size = document.getLong("size")?.toInt()
                    val points = document.getLong("points")?.toInt()
                    val redeemCoffee=RedeemCoffee(tempCoffee,date,size!!,points!!)
                    redeemCoffees.add(redeemCoffee)
                }
            }
        }

        fun getCurrentNoOfCarts(): Int{
            return sharedPreferences.getInt("number-of-carts",0) //trả về số lượng giỏ hàng cho tới hiện tại
        }

        fun increaseCart(){
            sharedPreferences.edit().putInt("number-of-carts",getCurrentNoOfCarts()+1) //tăng số lượng cart lên
        }

        fun initCarts(){
            if (sharedPreferences.getBoolean("online_acc",false)){
                initCartsFromFirebase()
            }
            else{
                initCartsLocally()
            }
        }

        //load tất cả các cart
        private fun initCartsFromFirebase(){
            val getCart = db.collection("cart" + Firebase.auth.currentUser!!.uid)

            getCart.get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val cart = document.get("cart") as ArrayList<String> //array field cart
                        var currentCart=Cart()
                        for (cartDesc in cart) {
                            val split = cartDesc.split(',') //tách từ theo dấu phẩy

                            //tìm cà phê
                            val temp = AppController.listCoffee
                            temp.sortedBy { it.getName() }
                            val tempCoffee =
                                temp[AppController.listCoffee.binarySearch(split[0], { obj1, obj2 ->
                                    (obj1 as Coffee).getName().compareTo((obj2 as Coffee).getName())
                                })] //tìm object cà phê tương ứng
                            val coffeeInCart = CoffeeInCart(tempCoffee)
                            coffeeInCart.changeQuantity(split[2].toInt())
                            coffeeInCart.changeSize(split[1].toInt())
                            currentCart.addToCart(coffeeInCart)
                        }
                        carts.add(currentCart) //thêm vào danh sách các cart
                    }

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
            for (line in lines) {
                if (line.isNotBlank()) {
                    val split = line.split(',') //tách từ theo dấu phẩy

                    //tìm cà phê
                    val temp = AppController.listCoffee
                    temp.sortedBy { it.getName() }
                    val tempCoffee =
                        temp[AppController.listCoffee.binarySearch(split[0], { obj1, obj2 ->
                            (obj1 as Coffee).getName().compareTo((obj2 as Coffee).getName())
                        })] //tìm object cà phê tương ứng
                    val coffeeInCart = CoffeeInCart(tempCoffee)
                    coffeeInCart.changeQuantity(split[2].toInt())
                    coffeeInCart.changeSize(split[1].toInt())
                    currentCart.addToCart(coffeeInCart)
                } else {
                    val temp = currentCart
                    carts.add(temp)
                    currentCart=Cart() //xoá cart hiện tại
                }

            }


        }

    }

}

