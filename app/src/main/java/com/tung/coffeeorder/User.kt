package com.tung.coffeeorder

import android.location.Address
import android.util.Log
import com.tung.coffeeorder.AppController.Companion.db
import com.tung.coffeeorder.AppController.Companion.sharedPreferences

class User private constructor(){
    private var id=""
    private var fullName=""
    private var phoneNumber=""
    private var email=""
    private var address=""

    companion object{
        @JvmStatic
        var singleton=User() //singleton
    }

    inner class LoyaltyPoint(){
        private var currentPoints=-0
        private var loyaltyCardCount=0

        fun getCurrentPoints(): Int{
            return currentPoints
        }

        fun getLoyaltyCardCount(): Int{
            return loyaltyCardCount
        }

        fun increaseLoyaltyCard(){
            if (++loyaltyCardCount>8){
                loyaltyCardCount=0
            }
        }

        fun addPoints(amount: Int){
            currentPoints+=amount //cộng điểm cho người dùng
        }

        fun removePoints(amount: Int){
            currentPoints-=amount
        }
    }

    val loyalty=LoyaltyPoint()

    fun editName(newFullName: String, initialize: Boolean=true){
        this.fullName=newFullName

        if (!initialize)
        update()
    }

    fun editPhoneNumber(newPhoneNumber: String, initialize: Boolean=true){
        this.phoneNumber=newPhoneNumber

        if (!initialize)
            update()
    }

    fun editEmail(newEmail: String, initialize: Boolean=true){
        this.email=newEmail

        if (!initialize)
            update()
    }

    fun editAddress(newAddress: String, initialize: Boolean=true){
        this.address=newAddress

        if (!initialize)
            update()
    }

    fun getname(): String{
        return fullName
    }

    fun getphoneNumber(): String{
        return phoneNumber
    }

    fun getemail(): String{
        return email
    }

    fun getaddress(): String{
        return address
    }

    fun edit(id: String, name: String, email: String, phoneNumber: String, address: String, create: Boolean=false){
        initialize(id,name,email,phoneNumber, address)

        if (!create) {
            update() //cập nhật thay đổi
        }
        else{
            //create là để biết gọi từ hàm signUp
            //tạo trên firebase
            val userData = mapOf(
                "email" to email,
                "name" to fullName,
                "phone-number" to phoneNumber,
                "address" to address
            )

            //cập nhật lên firebase thay đổi mới
            db.collection("users").document(id).set(userData)
        }
    }

    fun initialize(id: String, name: String, email: String, phoneNumber: String, address: String){
        editEmail(email)
        editName(name)
        editPhoneNumber(phoneNumber)
        editAddress(address)

        this.id=id

    }

    //cập nhật thay đổi
    fun update(){
        //nếu dùng tài khoản online thì up lên firebase
        if (sharedPreferences.getBoolean("online_acc",false)){
            //update lên firebase
            val userData = mapOf(
                "email" to email,
                "name" to fullName,
                "phone-number" to phoneNumber,
                "address" to address
            )

            //cập nhật lên firebase thay đổi mới
            db.collection("users").document(this.id).update(userData)

        } //dành cho những người không dùng firebase
        else{
            val editor = sharedPreferences.edit()
            editor.putString("email",email).apply()
            editor.putString("name",fullName).apply()
            editor.putString("phone-number",phoneNumber).apply()
            editor.putString("address",address).apply()

        }
    }

    //dành cho người không dùng acc onl
    fun loadLocal(){
        val email = sharedPreferences.getString("email","")!!
        val name = sharedPreferences.getString("name","")!!
        val phoneNumber = sharedPreferences.getString("phone-number","")!!
        val address = sharedPreferences.getString("address","")!!
        edit("",name,email,phoneNumber,address)
    }

}