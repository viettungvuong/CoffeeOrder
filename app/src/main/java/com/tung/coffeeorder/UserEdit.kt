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
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tung.coffeeorder.AccountFunctions.Companion.logout
import com.tung.coffeeorder.AccountFunctions.Companion.signOut
import com.tung.coffeeorder.AppController.Companion.sharedPreferences

class UserEdit : AppCompatActivity() {
    var editMode=ArrayList<Boolean>()
    var textFields=ArrayList<EditText>()
    var cancelButtons=ArrayList<ImageButton>()

    var fromAnonymousLogin=false

    override fun onStart() {
        super.onStart()
        fromAnonymousLogin=intent.getBooleanExtra("anonymouslogin",false)
        val backBtn = findViewById<ImageButton>(R.id.back_button)
        val signOutBtn = findViewById<MaterialButton>(R.id.signOutBtn)

        if (fromAnonymousLogin){ //từ anonymous login qua thì nút sign out là nút để vào màn hình chính
            //ẩn nút back
            backBtn.setVisibility(View.GONE) //ẩn nút back nếu đi vào từ anonymouslogin
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
                    logout(this)
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

        for (i in 0..4){
            editMode.add(false) //thêm editmode = false cho 4 field
        }

        textFields.add(findViewById<EditText>(R.id.userName))
        textFields.add(findViewById<EditText>(R.id.userEmail))
        textFields.add(findViewById<EditText>(R.id.userPhone))
        textFields.add(findViewById<EditText>(R.id.userAddress))

        cancelButtons.add(findViewById(R.id.secondaryProfileBtn))
        cancelButtons.add(findViewById(R.id.secondaryEmailBtn))
        cancelButtons.add(findViewById(R.id.secondaryPhoneBtn))
        cancelButtons.add(findViewById(R.id.secondaryAddressBtn))

        for (textField in textFields){
            textField.isEnabled=false //disable toàn bộ textField
        }

        textFields[0].text = Editable.Factory.getInstance().newEditable(User.singleton.getname())
        textFields[1].text = Editable.Factory.getInstance().newEditable(User.singleton.getemail())
        textFields[2].text = Editable.Factory.getInstance().newEditable(User.singleton.getphoneNumber())
        textFields[3].text = Editable.Factory.getInstance().newEditable(User.singleton.getaddress())

        val nameBtn = findViewById<ImageButton>(R.id.mainProfileBtn)
        val emailBtn = findViewById<ImageButton>(R.id.mainEmailBtn)
        val phoneBtn = findViewById<ImageButton>(R.id.mainPhoneBtn)
        val addressBtn = findViewById<ImageButton>(R.id.mainAddressBtn)

        nameBtn.setOnClickListener { view ->
            change(view,0)
        }

        emailBtn.setOnClickListener { view ->
            change(view,1)
        }

        phoneBtn.setOnClickListener { view ->
            change(view,2)
        }

        addressBtn.setOnClickListener { view ->
            change(view,3)
        }
    }

    fun change(view: View, index: Int){

        val temp = textFields[index].text.toString()

        if (!editMode[index]){
            cancelButtons[index].setVisibility(View.VISIBLE) //hiện nút cancel
            editMode[index] = true
            textFields[index].isEnabled = true

            cancelButtons[index].setImageResource(R.drawable.cancel)
            cancelButtons[index].setOnClickListener { v ->
                cancelEdit(v,view as ImageButton,index)
                textFields[index].text= Editable.Factory.getInstance().newEditable(temp)
            }

            (view as ImageButton).setImageResource(R.drawable.save_change)

            textFields[index].requestFocus()
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(textFields[index], InputMethodManager.SHOW_IMPLICIT)

            textFields[index].setSelection(textFields[index].length()) //đặt con trỏ ở cuối vị trí của string trong edittext
        }
        else{ //lúc này nút là nút Accept, bấm là lưu thay đổi
            val newText = textFields[index].text.toString()
            when (index){
                0->{
                    User.singleton.editName(newText)
                }
                1->{
                    User.singleton.editEmail(newText)
                }
                2->{
                    if (newText.isBlank()){
                        Toast.makeText(
                            this,
                            "Bạn chưa nhập số điện thoại",
                            Toast.LENGTH_LONG,
                        ).show()
                        return
                    }
                    User.singleton.editPhoneNumber(newText)
                }
                3->{
                    if (newText.isBlank()){
                        Toast.makeText(
                            this,
                            "Bạn chưa nhập địa chỉ",
                            Toast.LENGTH_LONG,
                        ).show()
                        return
                    }
                    User.singleton.editAddress(newText)
                }
            }
            cancelEdit(cancelButtons[index],view as ImageButton,index)
        }
    }

    fun cancelEdit(view: View, mainBtn: ImageButton, index: Int) {

        editMode[index] = false
        textFields[index].isEnabled=false

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