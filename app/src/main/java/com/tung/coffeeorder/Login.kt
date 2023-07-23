package com.tung.coffeeorder

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
                //xác nhận thất bại
                Toast.makeText(
                    applicationContext,
                    "Đăng nhập thất bại",
                    Toast.LENGTH_LONG,
                ).show()

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                //đã gửi mã ôtp
                storedVerificationId = verificationId
                resendToken = token
            }
        }

        val signInBtn = findViewById<MaterialButton>(R.id.signInBtn)
        signInBtn.setOnClickListener(
            View.OnClickListener {
                sendVerificationCode(findViewById<TextInputEditText>(R.id.username).text.toString()) //gửi mã xác nhận
            }
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

    }

    fun sendVerificationCode(phoneNumber: String){
        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
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