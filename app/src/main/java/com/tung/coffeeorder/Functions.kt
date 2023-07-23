package com.tung.coffeeorder

import android.content.Context
import android.media.Image
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.tung.coffeeorder.AppController.Companion.db
import com.tung.coffeeorder.AppController.Companion.redeemCoffees
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
                    val coffeeName = document.id
                    val temp = AppController.listCoffee
                    temp.sortedBy { it.getName() }
                    val tempCoffee =
                        temp[AppController.listCoffee.binarySearch(coffeeName, { obj1, obj2 ->
                            (obj1 as Coffee).getName().compareTo((obj2 as Coffee).getName())
                        })] //tìm object cà phê tương ứng
                    val date = LocalDateTime.ofInstant(document.getDate("valid-date")?.toInstant(), ZoneId.systemDefault())
                    val size = document.getLong("size")?.toInt()
                    val points = document.getLong("points")?.toInt()
                    val redeemCoffee=RedeemCoffee(tempCoffee,date,size!!,points!!)
                    redeemCoffees.add(redeemCoffee)
                }
            }
        }
    }

}

