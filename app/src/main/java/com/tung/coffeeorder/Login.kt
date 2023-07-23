package com.tung.coffeeorder

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tung.coffeeorder.AccountFunctions.Companion.signIn
import java.util.concurrent.TimeUnit

class Login : AppCompatActivity() {


    override fun onStart() {
        super.onStart()
        FirebaseApp.initializeApp(this)
        Functions.initCoffeeList(AppController.listCoffee)

        val currentUser = Firebase.auth.currentUser

        //đã có đăng nhập rồi
        if (currentUser != null) {

            Toast.makeText(
                this,
                "Đã đăng nhập thành công",
                Toast.LENGTH_SHORT,
            ).show()

            val email = Firebase.auth.currentUser!!.email.toString()

            AccountFunctions.getInfoFromFirebase(
                User.singleton
            ) { name, phoneNumber, address ->
                User.singleton.edit(name, email, phoneNumber, address)
                val intent =
                    Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            //đã đăng nhập rồi vào luôn
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
            //vào luôn main activity
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        val userInput = findViewById<TextInputEditText>(R.id.username)
        val passwordInput = findViewById<TextInputEditText>(R.id.password)

        val loginBtn = findViewById<MaterialButton>(R.id.signInBtn)
        loginBtn.setOnClickListener{
            startLogin(userInput,passwordInput) //ta không thể truyền thẳng string vào được
            //vì nó sẽ luôn là null (do lúc onCreate string vốn đâu có)
            //ta truyền view để nó lấy đúng text tại thời điểm
        }

        val signUpBtn = findViewById<MaterialButton>(R.id.signUpBtn)
        signUpBtn.setOnClickListener{
            val intent = Intent(this,Signup::class.java)
            startActivity(intent)
            finish()
        }

        //báo hiệu input cuối cùng để có thể bấm enter là vào
        findViewById<TextInputEditText>(R.id.password).setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
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

    fun startLogin(userInput: TextInputEditText, passwordInput: TextInputEditText){
        signIn(this,this,userInput.text.toString(), passwordInput.text.toString())
    }


}