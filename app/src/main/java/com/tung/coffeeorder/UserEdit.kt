package com.tung.coffeeorder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.EditText
import android.widget.ImageButton
import com.tung.coffeeorder.AppController.Companion.user

class UserEdit : AppCompatActivity() {
    lateinit var currentuser: User
    override fun onStart() {
        super.onStart()
        this.currentuser=user
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

    fun changeName(){

    }

    fun changeEmail(){

    }

    fun changePhone(){

    }

    fun changeAddress(){

    }
}