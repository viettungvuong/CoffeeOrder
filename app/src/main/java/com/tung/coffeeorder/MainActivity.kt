package com.tung.coffeeorder

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tung.coffeeorder.adapters.OrderAdapter


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

class BottomNavigationHandler(activity: Activity, navBar: BottomNavigationView) {
    //dùng class này để quản lý bottom nav bar gọn hơn
    init {
        var currentSelected=0
        navBar.selectedItemId=currentSelected //đặt index cho bottom nav bar

        navBar.setOnItemSelectedListener { item ->
            //khi bấm vào sẽ mở fragment tương ứng
            lateinit var selectedFragment: Fragment

            when (item.itemId) {
                R.id.home -> {
                    selectedFragment=Home()
                }
                R.id.rewards-> {
                    selectedFragment=Rewards()
                }
                R.id.orders -> {
                    selectedFragment=Orders()
                }
            }

            if (selectedFragment!=null){
                (activity as FragmentActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment,selectedFragment).commit() //hiện fragment lên
            }

            return@setOnItemSelectedListener true
        }
    }
}