package com.tung.coffeeorder

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tung.coffeeorder.AppController.Companion.currentCart
import com.tung.coffeeorder.AppController.Companion.resetAll
import com.tung.coffeeorder.adapters.OrderAdapter

const val firstFragmentTag = "Home"
const val secondFragmentTag = "Rewards"
const val thirdFragmentTag = "Orders"
public const val callingActivityExtra = "CallingActivity"

class MainActivity : AppCompatActivity() {
    lateinit var bottomNavigationHandler: BottomNavigationHandler

    override fun onStart() {
        super.onStart()

        val caller = intent?.getStringExtra(callingActivityExtra)
        if (caller != null) {
            if (caller == "OrderSuccess") {
                (this as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment, Orders())
                    .addToBackStack(thirdFragmentTag).commit() //hiện fragment lên
            }
            else if (caller=="CoffeeView")
            {
                (this as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment, Home())
                    .addToBackStack(firstFragmentTag).commit() //hiện fragment lên
            }
        }
        //khi mà quay lại actvitiy mà caller là OrderSuccess thì nó sẽ mở đúng tab

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //lấy số cart và order cho tới nay
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //từ đây mới có order và resume
        AppController.sharedPreferences.edit().putBoolean("first_time", false) //không còn lần đầu dùng

        AppController.ongoingAdapter =
            OrderAdapter(this, AppController.ongoingOrders, OngoingFragment())
        AppController.historyAdapter =
            OrderAdapter(this, AppController.historyOrders, HistoryFragment())


        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        supportFragmentManager.beginTransaction().replace(R.id.fragment, Home()).addToBackStack(
            firstFragmentTag
        ).commit() //hiện fragment Home đầu tiên

        bottomNavigationHandler =
            BottomNavigationHandler(this, bottomNavigationView) //handler bottom navigation view

    }

    override fun onResume() {
        super.onResume()

        //xử lý tab trên bottom navigation sẽ hiện tương ứng theo fragment đang được chọn
        setBottomNavChecked()
    }

    private fun setBottomNavChecked() {
        if (supportFragmentManager.backStackEntryCount > 0) {

            val fragment =
                supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name
            when (fragment) {
                firstFragmentTag -> findViewById<BottomNavigationView>(R.id.bottom_navigation).menu.findItem(
                    R.id.home
                ).setChecked(true)
                secondFragmentTag -> findViewById<BottomNavigationView>(R.id.bottom_navigation).menu.findItem(
                    R.id.rewards
                ).setChecked(true)
                thirdFragmentTag -> findViewById<BottomNavigationView>(R.id.bottom_navigation).menu.findItem(
                    R.id.orders
                ).setChecked(true)
            }
        }
        //khi mà quay lại actvitiy mà caller là OrderSuccess thì nó sẽ mở đúng tab
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 1) {
            supportFragmentManager.popBackStackImmediate() //quay về fragment trước trong main activity
            //thêm chữ immediate là để cập nhật luôn
            setBottomNavChecked()
        } else {
            finishAffinity()
            System.exit(0) //thoát khỏi app luôn
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        resetAll() //xoá hết mọi thứ hiện tại

        AppController.listCoffee.clear() //xoá danh sách cà phê
    }

    class BottomNavigationHandler(activity: Activity, navBar: BottomNavigationView) {
        //dùng class này để quản lý bottom nav bar gọn hơn
        init {
            var currentSelected = 0
            navBar.selectedItemId = currentSelected //đặt index cho bottom nav bar

            navBar.setOnItemSelectedListener { item ->
                //khi bấm vào sẽ mở fragment tương ứng
                lateinit var selectedFragment: Fragment

                var tag = ""

                when (item.itemId) {
                    R.id.home -> {
                        selectedFragment = Home()
                        tag = firstFragmentTag
                    }
                    R.id.rewards -> {
                        selectedFragment = Rewards()
                        tag = secondFragmentTag
                    }
                    R.id.orders -> {
                        selectedFragment = Orders()
                        tag = thirdFragmentTag
                    }
                }

                if (selectedFragment != null) {
                    (activity as FragmentActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment, selectedFragment)
                        .addToBackStack(tag).commit() //hiện fragment lên
                }

                return@setOnItemSelectedListener true
            }
        }
    }
}