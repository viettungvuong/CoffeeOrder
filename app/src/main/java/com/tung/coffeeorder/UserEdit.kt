package com.tung.coffeeorder

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.opengl.Visibility
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tung.coffeeorder.AccountFunctions.Companion.signOut
import com.tung.coffeeorder.AppController.Companion.sharedPreferences

class UserEdit : AppCompatActivity() {


    private lateinit var userName: EditText
    private lateinit var userEmail: EditText
    private lateinit var userPhone: EditText
    private lateinit var userAddress: EditText

    var editMode = false

    var fromAnonymousLogin=false

    override fun onStart() {
        super.onStart()
        fromAnonymousLogin=intent.getBooleanExtra("anonymouslogin",false)
        val backBtn = findViewById<ImageButton>(R.id.back_button)
        val signOutBtn = findViewById<MaterialButton>(R.id.signOutBtn)

        if (fromAnonymousLogin){ //từ anonymous login qua thì nút sign out là nút để vào màn hình chính
            //ẩn nút back
            backBtn.setVisibility(View.INVISIBLE) //ẩn nút back nếu đi vào từ anonymouslogin
            signOutBtn.text="Lưu thay đổi"
            val greenColor = ColorStateList.valueOf(Color.parseColor("#007B5E"))
            signOutBtn.backgroundTintList = greenColor //chuyển button này để lưu thay đổi
            signOutBtn.setOnClickListener(
                View.OnClickListener { //mở main activity nếu đủ thông tin
                    saveChangesForAnonymous(User.singleton)
                }
            )
        }
        else{
            backBtn.setVisibility(View.VISIBLE)
            backBtn.setOnClickListener(
                View.OnClickListener {
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    finish() //quay về activity trước
                }
            )
            signOutBtn.setOnClickListener(
                View.OnClickListener {
                    signOut(this)
                    sharedPreferences.edit().putBoolean("online_acc",true).apply()
                    //để nó không tự đăng nhập lại vào acc anonymous, mà Firebase auth cũng kh có tài khoản
                    //là hiện ra màn hình đăng nhập
                    val intent = Intent(this,Login::class.java)
                    startActivity(intent)
                    finish()

                }
            )
        }
        //mỗi lần mở lên kiểm tra có phải là từ anonymouslogin hay kh
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_activity)


        userName = findViewById<EditText>(R.id.userName)
        userEmail = findViewById<EditText>(R.id.userEmail)
        userPhone = findViewById<EditText>(R.id.userPhone)
        userAddress = findViewById<EditText>(R.id.userAddress)

        userName.text = Editable.Factory.getInstance().newEditable(User.singleton.getname())
        userEmail.text = Editable.Factory.getInstance().newEditable(User.singleton.getemail())
        userPhone.text = Editable.Factory.getInstance().newEditable(User.singleton.getphoneNumber())
        userAddress.text = Editable.Factory.getInstance().newEditable(User.singleton.getaddress())

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
        val temp = userName.text.toString()

        if (!editMode){
            cancelButton.setVisibility(View.VISIBLE) //hiện nút
            editMode = true
            userName.isEnabled = true
            userEmail.isEnabled = false
            userPhone.isEnabled = false
            userAddress.isEnabled = false

            cancelButton.setImageResource(R.drawable.cancel)
            cancelButton.setOnClickListener { v ->
                cancelEdit(v,view as ImageButton)
                userName.text= Editable.Factory.getInstance().newEditable(temp)
            }

            (view as ImageButton).setImageResource(R.drawable.save_change)

            userName.requestFocus()
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(userName, InputMethodManager.SHOW_IMPLICIT)
            userName.setSelection(userName.length())
        }
        else{ //lúc này nút là nút Accept, bấm là lưu thay đổi
            User.singleton.editName(userName.text.toString())
            cancelEdit(cancelButton,view as ImageButton)
        }
    }

    fun changeEmail(view: View) {
        val cancelButton = findViewById<ImageButton>(R.id.secondaryEmailBtn)
        val temp = userEmail.text.toString()

        if (!editMode) {
            cancelButton.setVisibility(View.VISIBLE) //hiện nút
            editMode = true
            userEmail.isEnabled = true
            userName.isEnabled = false
            userPhone.isEnabled = false
            userAddress.isEnabled = false


            cancelButton.setImageResource(R.drawable.cancel)
            cancelButton.setOnClickListener { v ->
                cancelEdit(v,view as ImageButton)
                userEmail.text= Editable.Factory.getInstance().newEditable(temp)
            }

            (view as ImageButton).setImageResource(R.drawable.save_change)

            userEmail.requestFocus()
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(userEmail, InputMethodManager.SHOW_IMPLICIT)
            userEmail.setSelection(userEmail.length())
        }
        else{ //lúc này đã là nút Accept
            User.singleton.editEmail(userEmail.text.toString())
            cancelEdit(cancelButton,view as ImageButton)
        }
    }

    fun changePhone(view: View) {
        val cancelButton = findViewById<ImageButton>(R.id.secondaryPhoneBtn)
        val temp = userPhone.text.toString()

        if (!editMode) {
            cancelButton.setVisibility(View.VISIBLE) //hiện nút
            editMode = true
            userPhone.isEnabled = true
            userName.isEnabled = false
            userEmail.isEnabled = false
            userAddress.isEnabled = false


            cancelButton.setImageResource(R.drawable.cancel)
            cancelButton.setOnClickListener { v ->
                cancelEdit(v,view as ImageButton)
                userPhone.text= Editable.Factory.getInstance().newEditable(temp)
            }

            (view as ImageButton).setImageResource(R.drawable.save_change)


            userPhone.requestFocus()
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(userPhone, InputMethodManager.SHOW_IMPLICIT)
            userPhone.setSelection(userPhone.length())
        }
        else{ //lúc này đã là nút Accept
            if (userPhone.text.isBlank()){
                Toast.makeText(
                    this,
                    "Bạn chưa nhập số điện thoại",
                    Toast.LENGTH_LONG,
                ).show()
                return
            }
            User.singleton.editPhoneNumber(userPhone.text.toString())
            cancelEdit(cancelButton,view as ImageButton)
        }
    }

    fun changeAddress(view: View) {
        val cancelButton = findViewById<ImageButton>(R.id.secondaryAddressBtn)
        val temp = userAddress.text.toString()

        if (!editMode) {
            cancelButton.setVisibility(View.VISIBLE) //hiện nút
            editMode = true
            userAddress.isEnabled = true
            userName.isEnabled = false
            userPhone.isEnabled = false
            userEmail.isEnabled = false


            cancelButton.setImageResource(R.drawable.cancel)
            cancelButton.setOnClickListener { v ->
                cancelEdit(v,view as ImageButton)
                userAddress.text= Editable.Factory.getInstance().newEditable(temp)
            }

            (view as ImageButton).setImageResource(R.drawable.save_change)


            userAddress.requestFocus()
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(userAddress, InputMethodManager.SHOW_IMPLICIT)
            userAddress.setSelection(userAddress.length())
        }
        else{ //lúc này đã là nút Accept
            if (userAddress.text.isBlank()){
                Toast.makeText(
                    this,
                    "Bạn chưa nhập địa chỉ",
                    Toast.LENGTH_LONG,
                ).show()
                return
            }
            User.singleton.editAddress(userAddress.text.toString())
            cancelEdit(cancelButton,view as ImageButton)
        }
    }

    fun cancelEdit(view: View, mainBtn: ImageButton) {

        editMode = false
        userAddress.isEnabled = false
        userName.isEnabled = false
        userPhone.isEnabled = false
        userEmail.isEnabled = false

        view.setVisibility(View.INVISIBLE) //ẩn nút
        mainBtn.setImageResource(R.drawable.edit)
    }

    fun saveChangesForAnonymous(user: User){
        if (user.getaddress().isBlank()) {
            Toast.makeText(
                this,
                "Bạn chưa nhập địa chỉ",
                Toast.LENGTH_LONG,
            ).show()
        }
        else if (user.getphoneNumber().isBlank()){
            Toast.makeText(
                this,
                "Bạn chưa nhập số điện thoại",
                Toast.LENGTH_LONG,
            ).show()
        }
        else{ //đủ thông tin rồi thì mở main activity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

}