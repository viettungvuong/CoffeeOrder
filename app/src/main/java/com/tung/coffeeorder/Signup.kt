package com.tung.coffeeorder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class Signup : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup_activity)

        val email = findViewById<TextInputEditText>(R.id.username).text.toString()
        val password = findViewById<TextInputEditText>(R.id.password).text.toString()
        val name = findViewById<TextInputEditText>(R.id.name).text.toString()
        val phoneNumber = findViewById<TextInputEditText>(R.id.phoneNumber).text.toString()
        val address=findViewById<TextInputEditText>(R.id.address).text.toString()

        Log.d("email",email)
        Log.d("password",password)
        val signUpBtn = findViewById<MaterialButton>(R.id.signUpBtn)
        signUpBtn.setOnClickListener{
            AccountFunctions.signUp(this,this,email,password,name,phoneNumber, address)
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
    }
}