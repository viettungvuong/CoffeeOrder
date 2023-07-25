package com.tung.coffeeorder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tung.coffeeorder.AccountFunctions.Companion.signIn
import com.tung.coffeeorder.AppController.Companion.initCarts
import com.tung.coffeeorder.AppController.Companion.initCoffeeList
import com.tung.coffeeorder.AppController.Companion.initRedeem
import com.tung.coffeeorder.AppController.Companion.retrieveCurrentNoOfCarts
import com.tung.coffeeorder.AppController.Companion.retrieveCurrentNoOfOrders
import com.tung.coffeeorder.AppController.Companion.sharedPreferences

class Login : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) //chặn dark mode

        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        FirebaseApp.initializeApp(this)
        initCoffeeList(AppController.listCoffee)

        sharedPreferences = getSharedPreferences("Coffee-App-Prefs", Context.MODE_PRIVATE) //dùng sharedprerences để lưu vài thông tin
//        sharedPreferences.edit().putInt("number-of-carts",0).apply()
//       sharedPreferences.edit().putInt("number-of-orders",0).apply()
//        sharedPreferences.edit().putInt("number-of-redeems",0).apply()

        initRedeem() //lấy danh sách các redeem coffee

        autoLogin() //tự động đăng nhập

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

        val anonymousUse = findViewById<MaterialButton>(R.id.anonymous)
        anonymousUse.setOnClickListener{
            anonymousLogin()
        }

    }

    fun anonymousLogin(){
        sharedPreferences.edit().putBoolean("online_acc",false).apply() //đặt là không dùng tài khoản online
        retrieveCurrentNoOfCarts()
        retrieveCurrentNoOfOrders()
        initCarts(this) //lấy danh sách các cart
        User.singleton.loadLocal() //đọc thông tin local

        //nếu thiếu thông tin thì phải nhập
        if (User.singleton.getaddress().isBlank()||User.singleton.getphoneNumber().isBlank()) {
            Toast.makeText(
                this,
                "Bạn hãy nhập thông tin để tiếp tục",
                Toast.LENGTH_LONG,
            ).show()
            val intent = Intent(this, UserEdit::class.java)
            intent.putExtra("anonymouslogin", true) //để báo đây là anonymouslogin
            startActivity(intent) //mở userEdit để người dùng nhập thông tin
        }
        else{
            //đủ thông tin thì vào màn hình chính
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent) //mở userEdit để người dùng nhập thông tin
        }
    }

    fun startLogin(userInput: TextInputEditText, passwordInput: TextInputEditText){
        signIn(this,this,userInput.text.toString(), passwordInput.text.toString())
    }

    fun autoLogin(){
        if (sharedPreferences.getBoolean("online_acc", false)) {
            if (Firebase.auth.currentUser != null) {
                val intent =
                    Intent(this, MainActivity::class.java)

                sharedPreferences.edit()
                    .putBoolean("online_acc", true)
                    .apply() //ghi nhận là dùng tài khoản online cho app

                Toast.makeText(
                    this,
                    "Đã đăng nhập thành công",
                    Toast.LENGTH_SHORT,
                ).show()

                startActivity(intent)

                val email = Firebase.auth.currentUser!!.email.toString()

                AppController.getInfoFromFirebase(
                    User.singleton
                ) { id, name, phoneNumber, address,loyaltyPoint ->
                    User.singleton.initialize(
                        Firebase.auth.currentUser!!.uid,
                        name,
                        email,
                        phoneNumber,
                        address,
                        loyaltyPoint
                    )
                    initCarts(this) //lấy danh sách các cart
                    retrieveCurrentNoOfCarts()
                    retrieveCurrentNoOfOrders()

                    finish()
                }
            }
        }
        else{
            anonymousLogin()
        }
    }

}