package com.tung.coffeeorder

import android.app.Activity
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavigationHandler(activity: Activity, navBar: BottomNavigationView) {
    //dùng class này để quản lý bottom nav bar gọn hơn
    init {
        var currentSelected=0

        when (activity) {
            is MainActivity -> {
                currentSelected = R.id.home
            }
            is Rewards -> {
                currentSelected = R.id.rewards
            }
            is Orders -> {
                currentSelected = R.id.orders
            }
        }

        navBar.selectedItemId=currentSelected //đặt index cho bottom nav bar

        navBar.setOnItemSelectedListener { item ->
            // do stuff
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(activity, MainActivity::class.java)
                    activity.startActivity(intent)
                }
                R.id.rewards-> {
                    val intent = Intent(activity, Rewards::class.java)
                    activity.startActivity(intent)
                }
                R.id.orders -> {
                    val intent = Intent(activity, Orders::class.java)
                    activity.startActivity(intent)
                }
            }

            return@setOnItemSelectedListener true
        }
    }
}