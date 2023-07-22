package com.tung.coffeeorder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.EditText

class UserEdit : AppCompatActivity() {
    lateinit var user: User
    override fun onStart() {
        super.onStart()
        this.user=user
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_activity)

        val userName = findViewById<EditText>(R.id.userName)
        val userEmail = findViewById<EditText>(R.id.userEmail)
        val userPhone = findViewById<EditText>(R.id.userPhone)
        val userAddress = findViewById<EditText>(R.id.userAddress)

        userName.text= Editable.Factory.getInstance().newEditable(user.getname())
        userEmail.text= Editable.Factory.getInstance().newEditable(user.getemail())
        userPhone.text= Editable.Factory.getInstance().newEditable(user.getphoneNumber())
        userAddress.text= Editable.Factory.getInstance().newEditable(user.getaddress())
    }
}