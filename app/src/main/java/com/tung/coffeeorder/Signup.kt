package com.tung.coffeeorder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.button.MaterialButton

class Signup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_activity)

        val email = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val name = findViewById<EditText>(R.id.name)
        val phoneNumber = findViewById<EditText>(R.id.phoneNumber)
        val address=findViewById<EditText>(R.id.address)

        val signUpBtn = findViewById<MaterialButton>(R.id.signUpBtn)
    }
}