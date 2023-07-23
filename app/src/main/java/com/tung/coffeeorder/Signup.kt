package com.tung.coffeeorder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.button.MaterialButton

class Signup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_activity)

        val email = findViewById<EditText>(R.id.username).text.toString()
        val password = findViewById<EditText>(R.id.password).text.toString()
        val name = findViewById<EditText>(R.id.name).text.toString()
        val phoneNumber = findViewById<EditText>(R.id.phoneNumber).text.toString()
        val address=findViewById<EditText>(R.id.address).text.toString()

        val signUpBtn = findViewById<MaterialButton>(R.id.signUpBtn)
        signUpBtn.setOnClickListener{
            AccountFunctions.signUp(this,this,email,password,name,phoneNumber, address)
        }
    }
}