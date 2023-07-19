package com.tung.coffeeorder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tung.coffeeorder.Functions.Companion.listCoffee
import java.util.LinkedList

class MainActivity : AppCompatActivity() {
    lateinit var bottomNavigationHandler: BottomNavigationHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().replace(R.id.fragment,Home()).commit() //hiện fragment Home đầu tiên

        val bottomNavigationView=findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationHandler=BottomNavigationHandler(this,bottomNavigationView) //handler bottom navigation view

        if (listCoffee==null){ //nếu danh sách coffee là null

        }
    }

    fun initCoffeeList(listCoffee: LinkedList<Coffee>){

    }
}