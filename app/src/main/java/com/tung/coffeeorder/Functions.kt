package com.tung.coffeeorder

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
        lateinit var listCoffee: LinkedList<Coffee> //danh sách các coffee
        val dbCoffeeList="coffee"
        val dbCoffeeNameField="name"
        val dbCoffeeImageField="imageName"

        @JvmStatic
        suspend fun getDownloadUrl(fileName: String): String {
            return suspendCoroutine { continuation ->
                val storageRef = FirebaseStorage.getInstance().reference
                val fileRef = storageRef.child(fileName)

                fileRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        continuation.resumeWith(Result.success(downloadUrl)) //trả về kết quả nếu được
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWith(Result.failure(exception))
                    }
            }
        }
    }

}