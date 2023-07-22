package com.tung.coffeeorder

import android.location.Address
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import com.tung.coffeeorder.AppController.Companion.user

class UserEdit : AppCompatActivity() {
    lateinit var currentuser: User

    lateinit var userName: EditText
    lateinit var userEmail: EditText
    lateinit var userPhone: EditText
    lateinit var userAddress: EditText

    var editMode=false
    override fun onStart() {
        super.onStart()
        this.currentuser=user
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_activity)

        userName = findViewById<EditText>(R.id.userName)
        userEmail = findViewById<EditText>(R.id.userEmail)
        userPhone = findViewById<EditText>(R.id.userPhone)
        userAddress = findViewById<EditText>(R.id.userAddress)

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

    fun changeName(view: View){
        editMode=true
        userName.isEnabled=true
        userEmail.isEnabled=false
        userPhone.isEnabled=false
        userAddress.isEnabled=false
    }

    fun changeEmail(view: View){
        editMode=true
        userEmail.isEnabled=true
        userName.isEnabled=false
        userPhone.isEnabled=false
        userAddress.isEnabled=false
    }

    fun changePhone(view: View){
        editMode=true
        userPhone.isEnabled=true
        userName.isEnabled=false
        userEmail.isEnabled=false
        userAddress.isEnabled=false
    }

    fun changeAddress(view: View){
        editMode=true
        userAddress.isEnabled=true
        userName.isEnabled=false
        userPhone.isEnabled=false
        userEmail.isEnabled=false
    }

    fun cancelEdit(view: View){
        editMode=false
        userAddress.isEnabled=false
        userName.isEnabled=false
        userPhone.isEnabled=false
        userEmail.isEnabled=false
    }
}