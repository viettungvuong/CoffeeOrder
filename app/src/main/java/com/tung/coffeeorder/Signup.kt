package com.tung.coffeeorder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class Signup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_activity)

        val email = findViewById<TextInputEditText>(R.id.username)
        val password = findViewById<TextInputEditText>(R.id.password)
        val name = findViewById<TextInputEditText>(R.id.name)
        val phoneNumber = findViewById<TextInputEditText>(R.id.phoneNumber)
        val address=findViewById<TextInputEditText>(R.id.address)


        val signUpBtn = findViewById<MaterialButton>(R.id.signUpBtn)
        signUpBtn.setOnClickListener{
            startSignup(email,password,name, phoneNumber, address)
        }

        findViewById<TextInputEditText>(R.id.address).setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // do something, e.g. set your TextView here via .setText()
                val imm: InputMethodManager =
                    v.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                return@OnEditorActionListener true
            }
            false
        })

        val backBtn = findViewById<ImageButton>(R.id.back_button)
        backBtn.setOnClickListener(
            View.OnClickListener {
                val intent = Intent(this,Login::class.java)
                startActivity(intent)
                finish() //quay về activity trước
            }
        )
    }

    fun startSignup(user: TextInputEditText, password: TextInputEditText, name: TextInputEditText,
    phoneNumber: TextInputEditText, address: TextInputEditText){
        AccountFunctions.signUp(
            this,
            this,
            user.text.toString(),
            password.text.toString(),
            name.text.toString(),
            phoneNumber.text.toString(),
            address.text.toString()
        )
    }
}