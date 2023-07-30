package com.tung.coffeeorder

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AccountFunctions {
    companion object{
        fun logout(context: Context){
            signOut(context)

            //xoá mọi thứ hiện tại
            AppController.resetAll()

            //ta để true để nó ra màn hình login, thay vì tiếp tục anonymous login
            AppController.sharedPreferences.edit().putBoolean("online_acc",true).apply()
        }
        @JvmStatic
        fun autoLogin(activity: Activity){
            if (AppController.sharedPreferences.getBoolean("first_time", true)){
                return //lần đầu dùng thì không vào auto login
            }
            if (AppController.sharedPreferences.getBoolean("online_acc", false)) {
                if (Firebase.auth.currentUser != null) {

                    AppController.sharedPreferences.edit()
                        .putBoolean("online_acc", true)
                        .apply() //ghi nhận là dùng tài khoản online cho app

                    Toast.makeText(
                        activity,
                        "Đã đăng nhập thành công",
                        Toast.LENGTH_SHORT,
                    ).show()



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

                        AppController.retrieveNoCartsOrders {
                            AppController.initCarts(activity) //lấy danh sách các cart, rồi resume cart, rồi lấy order

                            val intent =
                                Intent(activity, MainActivity::class.java)
                            activity.startActivity(intent)
                            activity.finish()
                        }

                    }
                }
            }
            else{
                anonymousLogin(activity)
            }
        }

        @JvmStatic
        fun anonymousLogin(activity: Activity){
            AppController.sharedPreferences.edit().putBoolean("online_acc",false).apply() //đặt là không dùng tài khoản online

            AppController.retrieveNoCartsOrders {
                AppController.initCarts(activity) //lấy danh sách các cart, rồi resume cart, rồi lấy order

                val intent =
                    Intent(activity, MainActivity::class.java)
                activity.startActivity(intent)
                activity.finish()

                User.singleton.loadLocal() //đọc thông tin local

                //nếu thiếu thông tin thì phải nhập
                if (User.singleton.getaddress().isBlank() || User.singleton.getphoneNumber()
                        .isBlank()
                ) {
                    Toast.makeText(
                        activity,
                        "Bạn hãy nhập thông tin để tiếp tục",
                        Toast.LENGTH_LONG,
                    ).show()
                    val intent = Intent(activity, UserEdit::class.java)
                    intent.putExtra("anonymouslogin", true) //để báo đây là anonymouslogin
                    activity.startActivity(intent) //mở userEdit để người dùng nhập thông tin
                } else {
                    //đủ thông tin thì vào màn hình chính
                    AppController.sharedPreferences.edit().putBoolean("first_time", false)
                        .apply() //không còn lần đầu dùng nữa
                    val intent = Intent(activity, MainActivity::class.java)
                    activity.startActivity(intent) //mở userEdit để người dùng nhập thông tin
                }
            }

        }

        @JvmStatic
        fun signIn(activity: Activity, context: Context, username: String, password: String){
            Firebase.auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {


                        AppController.sharedPreferences.edit()
                            .putBoolean("online_acc", true)
                            .apply() //ghi nhận là dùng tài khoản online cho app

                        Toast.makeText(
                            activity,
                            "Đã đăng nhập thành công",
                            Toast.LENGTH_SHORT,
                        ).show()

                        AppController.sharedPreferences.edit().putBoolean("first_time", false).apply() //không còn lần đầu dùng nữa

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

                            AppController.retrieveNoCartsOrders {
                                AppController.initCarts(activity) //lấy danh sách các cart
                                val intent =
                                    Intent(activity, MainActivity::class.java)
                                activity.startActivity(intent)
                                activity.finish()
                            }

                        }

                    } else {
                        Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            context,
                            "Không thể đăng nhập",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }

        @JvmStatic
        fun signUp(activity: Activity, context: Context, username: String, password: String, name: String, phoneNumber: String, address: String){
            Firebase.auth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(activity) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            context,
                            "Tạo tài khoản thành công",
                            Toast.LENGTH_SHORT,
                        ).show()

                        AppController.sharedPreferences.edit().putBoolean("first_time", false).apply() //không còn lần đầu dùng nữa

                        AppController.sharedPreferences.edit().putBoolean("online_acc",true).apply() //ghi nhận là dùng tài khoản online cho app

                        //đặt vào người dùng hiện tại
                        User.singleton.edit(Firebase.auth.currentUser!!.uid,name,username,phoneNumber,address,true)

                        val intent =
                            Intent(context,MainActivity::class.java)
                        activity.startActivity(intent)

                    } else {
                        Toast.makeText(
                            context,
                            "Không thể tạo tài khoản",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }

        @JvmStatic
        fun signOut(context: Context){
            Firebase.auth.signOut() //đăng xuất

            Toast.makeText(
                context,
                "Đã đăng xuất thành công",
                Toast.LENGTH_SHORT,
            ).show()

            AppController.sharedPreferences.edit().putBoolean("online_acc",false).apply()

        }

        @JvmStatic
        fun changePassword(context: Context, newPassword: String){
            val user = Firebase.auth.currentUser

            user!!.updatePassword(newPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            context,
                            "Đã đổi mật khẩu thành công",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }


    }
}