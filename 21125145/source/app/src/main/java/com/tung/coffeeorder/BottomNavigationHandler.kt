package com.tung.coffeeorder

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

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