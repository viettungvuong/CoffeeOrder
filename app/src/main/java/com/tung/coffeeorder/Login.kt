package com.tung.coffeeorder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tung.coffeeorder.AccountFunctions.Companion.signIn
import java.util.concurrent.TimeUnit

class Login : AppCompatActivity() {

    override fun onStart() {
        super.onStart()
        FirebaseApp.initializeApp(this)
        Functions.initCoffeeList(AppController.listCoffee)

        val currentUser = Firebase.auth.currentUser

        //đã có đăng nhập rồi
        if (currentUser != null) {

            Toast.makeText(
                this,
                "Đã đăng nhập thành công",
                Toast.LENGTH_SHORT,
            ).show()

            //đã đăng nhập rồi
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
            //vào luôn main activity
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        val userInput = findViewById<TextInputEditText>(R.id.username).toString()
        val passwordInput = findViewById<TextInputEditText>(R.id.password).toString()

        val loginBtn = findViewById<MaterialButton>(R.id.signInBtn)
        loginBtn.setOnClickListener{signIn(this,this,userInput, passwordInput)}

    }


}