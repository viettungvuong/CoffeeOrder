package com.tung.coffeeorder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.FirebaseApp

class Login : AppCompatActivity() {
    override fun onStart() {
        super.onStart()
        FirebaseApp.initializeApp(this)
        Functions.initCoffeeList(Functions.listCoffee)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)
    }
}