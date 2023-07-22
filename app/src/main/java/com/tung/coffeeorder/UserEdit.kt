package com.tung.coffeeorder

import android.location.Address
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.tung.coffeeorder.AppController.Companion.user

class UserEdit : AppCompatActivity() {
    lateinit var currentuser: User

    lateinit var userName: EditText
    lateinit var userEmail: EditText
    lateinit var userPhone: EditText
    lateinit var userAddress: EditText

    var editMode = false
    override fun onStart() {
        super.onStart()
        this.currentuser = user
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_activity)

        userName = findViewById<EditText>(R.id.userName)
        userEmail = findViewById<EditText>(R.id.userEmail)
        userPhone = findViewById<EditText>(R.id.userPhone)
        userAddress = findViewById<EditText>(R.id.userAddress)

        userName.text = Editable.Factory.getInstance().newEditable(user.getname())
        userEmail.text = Editable.Factory.getInstance().newEditable(user.getemail())
        userPhone.text = Editable.Factory.getInstance().newEditable(user.getphoneNumber())
        userAddress.text = Editable.Factory.getInstance().newEditable(user.getaddress())

        val nameBtn = findViewById<ImageButton>(R.id.mainProfileBtn)
        val emailBtn = findViewById<ImageButton>(R.id.mainEmailBtn)
        val phoneBtn = findViewById<ImageButton>(R.id.mainPhoneBtn)
        val addressBtn = findViewById<ImageButton>(R.id.mainAddressBtn)

        nameBtn.setOnClickListener { view ->
            changeName(view)
        }

        emailBtn.setOnClickListener { view ->
            changeEmail(view)
        }

        phoneBtn.setOnClickListener { view ->
            changePhone(view)
        }

        addressBtn.setOnClickListener { view ->
            changeAddress(view)
        }

    }

    fun changeName(view: View) {
        if (!editMode){
            editMode = true
            userName.isEnabled = true
            userEmail.isEnabled = false
            userPhone.isEnabled = false
            userAddress.isEnabled = false

            val cancelButton = findViewById<ImageButton>(R.id.secondaryProfileBtn)
            cancelButton.setImageResource(R.drawable.cancel)
            cancelButton.setOnClickListener{
                    view->cancelEdit(view)
            }

            (view as ImageButton).setImageResource(R.drawable.save_change)
        }
        else{
            AppController.user.editName(userName.text.toString())
        }
    }

    fun changeEmail(view: View) {
        if (!editMode) {
            editMode = true
            userEmail.isEnabled = true
            userName.isEnabled = false
            userPhone.isEnabled = false
            userAddress.isEnabled = false

            val cancelButton = findViewById<ImageButton>(R.id.secondaryEmailBtn)
            cancelButton.setImageResource(R.drawable.cancel)
            cancelButton.setOnClickListener { view ->
                cancelEdit(view)
            }

            (view as ImageButton).setImageResource(R.drawable.save_change)
        }
        else{
            AppController.user.editEmail(userEmail.text.toString())
        }
    }

    fun changePhone(view: View) {
        if (!editMode) {
            editMode = true
            userPhone.isEnabled = true
            userName.isEnabled = false
            userEmail.isEnabled = false
            userAddress.isEnabled = false

            val cancelButton = findViewById<ImageButton>(R.id.secondaryPhoneBtn)
            cancelButton.setImageResource(R.drawable.cancel)
            cancelButton.setOnClickListener { view ->
                cancelEdit(view)
            }

            (view as ImageButton).setImageResource(R.drawable.save_change)
        }
        else{
            AppController.user.editPhoneNumber(userPhone.text.toString())
        }
    }

    fun changeAddress(view: View) {
        if (!editMode) {
            editMode = true
            userAddress.isEnabled = true
            userName.isEnabled = false
            userPhone.isEnabled = false
            userEmail.isEnabled = false

            val cancelButton = findViewById<ImageButton>(R.id.secondaryAddressBtn)
            cancelButton.setImageResource(R.drawable.cancel)
            cancelButton.setOnClickListener { view ->
                cancelEdit(view)
            }

            (view as ImageButton).setImageResource(R.drawable.save_change)
        }
        else{
            AppController.user.editAddress(userAddress.text.toString())
        }
    }

    fun cancelEdit(view: View) {

        editMode = false
        userAddress.isEnabled = false
        userName.isEnabled = false
        userPhone.isEnabled = false
        userEmail.isEnabled = false

        view.setVisibility(View.INVISIBLE) //ẩn nút
    }

}