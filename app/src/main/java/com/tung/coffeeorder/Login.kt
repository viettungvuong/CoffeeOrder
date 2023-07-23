package com.tung.coffeeorder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class Login : AppCompatActivity() {
    lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    lateinit var storedVerificationId: String
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    var signInMode = true //true là nhập số điện thoại, false là nhập otp

    lateinit var textInput: TextInputEditText

    override fun onStart() {
        super.onStart()
        FirebaseApp.initializeApp(this)
        Functions.initCoffeeList(Functions.listCoffee)

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                //xác nhận thành công
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.d("Error",e.message.toString())
                if (e is FirebaseAuthInvalidCredentialsException){
                    Toast.makeText(
                        applicationContext,
                        "Sai định dạng số điện thoại",
                        Toast.LENGTH_LONG,
                    ).show()
                }
                else {
                    //gửi mã xác nhận thất bại
                    Toast.makeText(
                        applicationContext,
                        "Gửi mã xác nhận thất bại",
                        Toast.LENGTH_LONG,
                    ).show()
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                //đã gửi mã ôtp
                storedVerificationId = verificationId
                resendToken = token

                signInMode=false
                textInput.text= Editable.Factory.getInstance().newEditable("")
                textInput.hint="Mã xác nhận"
                findViewById<MaterialButton>(R.id.signInBtn).text="Xác nhận mã"
            }
        }

        val currentUser = Firebase.auth.currentUser

        //đã có đăng nhập rồi
        if (currentUser != null) {

            Toast.makeText(
                this,
                "Đã đăng nhập thành công",
                Toast.LENGTH_SHORT,
            ).show()

            //đã đăng nhập rồi
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
            //vào luôn main activity
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        textInput = findViewById(R.id.username)

        val signInBtn = findViewById<MaterialButton>(R.id.signInBtn)
        val cancelBtn = findViewById<MaterialButton>(R.id.cancelBtn)

        cancelBtn.setOnClickListener { view ->
            signInMode=true
            view.setVisibility(View.INVISIBLE) //ẩn nút
            signInBtn.text="Đăng nhập"
            textInput.text= Editable.Factory.getInstance().newEditable("")
            textInput.hint="Số điện thoại"
        }

        signInBtn.setOnClickListener {
           view->
            if (signInMode){
                cancelBtn.setVisibility(View.VISIBLE) //hiện nút chuyển qua tài khoản khác
                sendVerificationCode(
                    findViewById<TextInputEditText>(R.id.username).text.toString()
                ) //gửi mã xác nhận
            }
            else{
                verifyPhoneNumberWithCode(findViewById<TextInputEditText>(R.id.username).text.toString())
                //xác nhận mã otp
            }
        }


    }

    private fun verifyPhoneNumberWithCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(storedVerificationId, code)
        signInWithPhoneAuthCredential(credential)
    }

    fun sendVerificationCode(phoneNumber: String){
        //xử lý country code (+84)
        var phoneNumberWithCountryCode="+84"+phoneNumber.substring(1)
        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber(phoneNumberWithCountryCode) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Firebase.auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val FirebaseUser = task.result?.user
                    val name = FirebaseUser!!.displayName
                    val phoneNumber=FirebaseUser!!.phoneNumber
                    val email=FirebaseUser!!.email

                    AppController.user.editName(name!!)
                    AppController.user.editPhoneNumber(phoneNumber!!)
                    AppController.user.editEmail(email!!)

                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else { //đăng nhập thất bại
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(
                            applicationContext,
                            "Bạn đã nhập sai mã xác nhận, hãy kiểm tra lại",
                            Toast.LENGTH_LONG,
                        ).show()
                    }
                }
            }
    }


}