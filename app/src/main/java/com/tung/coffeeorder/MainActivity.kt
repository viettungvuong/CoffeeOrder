package com.tung.coffeeorder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.tung.coffeeorder.Functions.Companion.db
import com.tung.coffeeorder.Functions.Companion.dbCoffeeImageField
import com.tung.coffeeorder.Functions.Companion.dbCoffeeList
import com.tung.coffeeorder.Functions.Companion.dbCoffeeNameField
import com.tung.coffeeorder.Functions.Companion.listCoffee
import java.util.LinkedList

class MainActivity : AppCompatActivity() {
    lateinit var bottomNavigationHandler: BottomNavigationHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (listCoffee==null){ //nếu danh sách coffee là null
            initCoffeeList(listCoffee,db)
        }

        val bottomNavigationView=findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationHandler=BottomNavigationHandler(this,bottomNavigationView) //handler bottom navigation view

        supportFragmentManager.beginTransaction().replace(R.id.fragment,Home()).commit() //hiện fragment Home đầu tiên
    }

    fun initCoffeeList(listCoffee: LinkedList<Coffee>, db: FirebaseFirestore){
        db.collection(dbCoffeeList).get().addOnSuccessListener {
            documents->
            for (document in documents){
                val coffeeName = document.getString(dbCoffeeNameField)!!
                val imageName = document.getString(dbCoffeeImageField)!!
                //lấy dữ liệu cà phê

                val coffee = Coffee(coffeeName,imageName) //thêm cà phê vào linkedlist
                listCoffee.add(coffee)
            }
        }
    }
}