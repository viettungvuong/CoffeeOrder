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


    }

}

