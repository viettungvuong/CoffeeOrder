package com.tung.coffeeorder

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.tung.coffeeorder.AppController.Companion.user

class UserEdit : AppCompatActivity() {


    private lateinit var userName: EditText
    private lateinit var userEmail: EditText
    private lateinit var userPhone: EditText
    private lateinit var userAddress: EditText

    var editMode = false
    override fun onStart() {
        super.onStart()
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
        val cancelButton = findViewById<ImageButton>(R.id.secondaryProfileBtn)
        if (!editMode){
            editMode = true
            userName.isEnabled = true
            userEmail.isEnabled = false
            userPhone.isEnabled = false
            userAddress.isEnabled = false


            cancelButton.setImageResource(R.drawable.cancel)
            cancelButton.setOnClickListener{
                    view->cancelEdit(view)
            }

            (view as ImageButton).setImageResource(R.drawable.save_change)

            userName.requestFocus()
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(userName, InputMethodManager.SHOW_IMPLICIT)
        }
        else{
            AppController.user.editName(userName.text.toString())
            cancelEdit(cancelButton)
            (view as ImageButton).setImageResource(R.drawable.edit)
        }
    }

    fun changeEmail(view: View) {
        val cancelButton = findViewById<ImageButton>(R.id.secondaryEmailBtn)
        if (!editMode) {
            editMode = true
            userEmail.isEnabled = true
            userName.isEnabled = false
            userPhone.isEnabled = false
            userAddress.isEnabled = false


            cancelButton.setImageResource(R.drawable.cancel)
            cancelButton.setOnClickListener { view ->
                cancelEdit(view)
            }

            (view as ImageButton).setImageResource(R.drawable.save_change)

            userEmail.requestFocus()
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(userEmail, InputMethodManager.SHOW_IMPLICIT)
        }
        else{
            AppController.user.editEmail(userEmail.text.toString())
            cancelEdit(cancelButton)
            (view as ImageButton).setImageResource(R.drawable.edit)
        }
    }

    fun changePhone(view: View) {
        val cancelButton = findViewById<ImageButton>(R.id.secondaryPhoneBtn)
        if (!editMode) {
            editMode = true
            userPhone.isEnabled = true
            userName.isEnabled = false
            userEmail.isEnabled = false
            userAddress.isEnabled = false


            cancelButton.setImageResource(R.drawable.cancel)
            cancelButton.setOnClickListener { view ->
                cancelEdit(view)
            }

            (view as ImageButton).setImageResource(R.drawable.save_change)

            userPhone.requestFocus()
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(userPhone, InputMethodManager.SHOW_IMPLICIT)
        }
        else{
            AppController.user.editPhoneNumber(userPhone.text.toString())
            cancelEdit(cancelButton)
            (view as ImageButton).setImageResource(R.drawable.edit)
        }
    }

    fun changeAddress(view: View) {
        val cancelButton = findViewById<ImageButton>(R.id.secondaryAddressBtn)
        if (!editMode) {
            editMode = true
            userAddress.isEnabled = true
            userName.isEnabled = false
            userPhone.isEnabled = false
            userEmail.isEnabled = false


            cancelButton.setImageResource(R.drawable.cancel)
            cancelButton.setOnClickListener { view ->
                cancelEdit(view)
            }

            (view as ImageButton).setImageResource(R.drawable.save_change)

            userAddress.requestFocus()
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(userAddress, InputMethodManager.SHOW_IMPLICIT)
        }
        else{
            AppController.user.editAddress(userAddress.text.toString())
            cancelEdit(cancelButton)
            (view as ImageButton).setImageResource(R.drawable.edit)
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