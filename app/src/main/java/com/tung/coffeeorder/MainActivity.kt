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
import com.tung.coffeeorder.AppController.Companion.user
import com.tung.coffeeorder.AppController.Companion.db
import com.tung.coffeeorder.AppController.Companion.dbCoffeeImageField
import com.tung.coffeeorder.AppController.Companion.dbCoffeeList
import com.tung.coffeeorder.AppController.Companion.dbCoffeeNameField
import com.tung.coffeeorder.AppController.Companion.dbCoffeePriceField
import com.tung.coffeeorder.AppController.Companion.listCoffee
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.LinkedList

class MainActivity : AppCompatActivity() {
    lateinit var bottomNavigationHandler: BottomNavigationHandler

    override fun onStart() {
        super.onStart()




        //dummy address để test
        user.editName("Vương Quang Việt Tùng")
        user.editEmail("viettungvuong@gmail.com")
        user.editPhoneNumber("0785841999")
        user.editAddress("18 đường số 7, phường Tân Hưng, quận 7")

        AppController.ongoingAdapter = OrderAdapter(this, AppController.ongoingOrders, OngoingFragment())
        AppController.historyAdapter = OrderAdapter(this, AppController.historyOrders, HistoryFragment())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigationView=findViewById<BottomNavigationView>(R.id.bottom_navigation)

        supportFragmentManager.beginTransaction().replace(R.id.fragment,Home()).commit() //hiện fragment Home đầu tiên

        bottomNavigationHandler=BottomNavigationHandler(this,bottomNavigationView) //handler bottom navigation view

    }

    override fun onResume() {
        super.onResume()
        val caller=intent?.getStringExtra("CallingActivity")
        if (caller!=null){
            if (caller=="OrderSuccess"){
                val thirdMenuItem: MenuItem = findViewById<BottomNavigationView>(R.id.bottom_navigation).menu.getItem(2)
                thirdMenuItem.isChecked = true //mở orders fragment
                (this as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment,Orders()).commit() //hiện fragment lên
            }
        }

    }


}