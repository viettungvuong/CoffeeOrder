package com.tung.coffeeorder

import android.content.Context
import android.media.Image
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.*
import kotlin.coroutines.suspendCoroutine

class Functions {
    companion object{
        @JvmField
        var db= Firebase.firestore
        val storage = Firebase.storage.reference
        var listCoffee= LinkedList<Coffee>() //danh sách các coffee
        val dbCoffeeList="coffee"
        val dbCoffeeNameField="name"
        val dbCoffeeImageField="imageName"
        val dbCoffeePriceField="price"



//        @JvmStatic
//        suspend fun getDownloadUrl(fileName: String): String {
//            return suspendCoroutine { continuation ->
//                val storageRef = FirebaseStorage.getInstance().reference
//                val fileRef = storageRef.child(fileName)
//
//                fileRef.downloadUrl
//                    .addOnSuccessListener { uri ->
//                        val downloadUrl = uri.toString()
//                        continuation.resumeWith(Result.success(downloadUrl)) //trả về kết quả nếu được
//                    }
//                    .addOnFailureListener { exception ->
//                        continuation.resumeWith(Result.failure(exception))
//                    }
//            }
//        }


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
                strings.removeAt(strings.size - 1);
            }

            strings.reverse() //đảo ngược mảng

            moneyString = ""

            for (i in 0..strings.size - 1) {
                moneyString += strings[i]
            }

            return moneyString;
            //gio ta phai cho no xuat dung chieu
        }

//        @JvmStatic
//        fun initCoffeeList(listCoffee: LinkedList<Coffee>, db: FirebaseFirestore){
//            db.collection(dbCoffeeList).get().addOnSuccessListener {
//                    documents->
//                for (document in documents){
//                    Log.d("document",document.id)
//                    val coffeeName = document.getString(dbCoffeeNameField)!!
//                    Log.d("coffee name",coffeeName)
//                    val imageName = document.getString(dbCoffeeImageField)!!
//                    val price=document.getLong(dbCoffeePriceField)!!
//                    Log.d("coffee price",price.toString())
//                    //lấy dữ liệu cà phê
//
//                    val coffee = Coffee(coffeeName,imageName,price) //thêm cà phê vào linkedlist
//                    listCoffee.add(coffee)
//                }
//
//            }
//        }

        fun initCoffeeList(listCoffee: LinkedList<Coffee>){
            listCoffee.add(Coffee("Cà phê sữa đá","caphesuada",18000))
            listCoffee.add(Coffee("Cà phê muối","caphemuoi",19000))
        }

        fun imageFromCoffee(context: Context, coffee: Coffee): Int {
            return context.resources.getIdentifier(coffee.getImageFilename(),
            "drawable",
            context.packageName)
        }
    }

}