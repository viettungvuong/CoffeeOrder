package com.tung.coffeeorder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
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

        val nameBtn = findViewById<ImageButton>(R.id.mainProfileBtn)
        val emailBtn = findViewById<ImageButton>(R.id.mainEmailBtn)
        val phoneBtn = findViewById<ImageButton>(R.id.mainPhoneBtn)
        val addressBtn = findViewById<ImageButton>(R.id.mainAddressBtn)

        nameBtn.setOnClickListener(
            View.OnClickListener {
                changeName()
            }
        )

        emailBtn.setOnClickListener(
            View.OnClickListener {
                changeEmail()
            }
        )

        phoneBtn.setOnClickListener(
            View.OnClickListener {
                changePhone()
            }
        )

        addressBtn.setOnClickListener(
            View.OnClickListener {
                changeAddress()
            }
        )

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