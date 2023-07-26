package com.tung.coffeeorder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.tung.coffeeorder.AppController.Companion.db

import com.tung.coffeeorder.AppController.Companion.listCoffee
import com.tung.coffeeorder.AppController.Companion.fetchOrders
import com.tung.coffeeorder.AppController.Companion.initCarts
import com.tung.coffeeorder.AppController.Companion.resumeCart
import com.tung.coffeeorder.AppController.Companion.retrieveCurrentNoOfCarts
import com.tung.coffeeorder.AppController.Companion.retrieveCurrentNoOfOrders
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.LinkedList

class MainActivity : AppCompatActivity() {
    lateinit var bottomNavigationHandler: BottomNavigationHandler

    override fun onStart() {
        super.onStart()

        val caller=intent?.getStringExtra("CallingActivity")
        if (caller!=null){
            if (caller=="OrderSuccess"){
                val thirdMenuItem: MenuItem = findViewById<BottomNavigationView>(R.id.bottom_navigation).menu.getItem(2)
                thirdMenuItem.isChecked = true //mở orders fragment
                (this as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment,Orders()).commit() //hiện fragment lên
            }
        }
        //khi mà quay lại actvitiy mà caller là OrderSuccess thì nó sẽ mở đúng tab

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //lấy số cart và order cho tới nay
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //từ đây mới có order và resume

        AppController.ongoingAdapter = OrderAdapter(this, AppController.ongoingOrders, OngoingFragment())
        AppController.historyAdapter = OrderAdapter(this, AppController.historyOrders, HistoryFragment())


        val bottomNavigationView=findViewById<BottomNavigationView>(R.id.bottom_navigation)

        supportFragmentManager.beginTransaction().replace(R.id.fragment,Home()).commit() //hiện fragment Home đầu tiên

        bottomNavigationHandler=BottomNavigationHandler(this,bottomNavigationView) //handler bottom navigation view

    }


}