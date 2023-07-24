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

    inner class LoyaltyPoint{
        private var currentPoints=-0
        private var loyaltyCardCount=0

        fun getCurrentPoints(): Int{
            return currentPoints
        }

        fun getLoyaltyCardCount(): Int{
            return loyaltyCardCount
        }

        fun increaseLoyaltyCard(amount: Int){
            for (i in 1..amount){
                if (loyaltyCardCount+1<=8){
                    loyaltyCardCount++
                }
            }
        }

        fun addPoints(amount: Int){
            currentPoints+=amount //cộng điểm cho người dùng
        }

        fun removePoints(amount: Int){
            currentPoints-=amount
        }

        fun resetLoyaltyCard(){
            loyaltyCardCount=0
        }
    }

    val loyalty=LoyaltyPoint()

    fun editName(newFullName: String){
        this.fullName=newFullName

        update() //cập nhật thay đổi
    }

    fun editPhoneNumber(newPhoneNumber: String){
        this.phoneNumber=newPhoneNumber

        update() //cập nhật thay đổi
    }

    fun editEmail(newEmail: String){
        this.email=newEmail

        update() //cập nhật thay đổi
    }

    fun editAddress(newAddress: String){
        this.address=newAddress

        update() //cập nhật thay đổi
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

        //nếu không gọi từ signup
        if (!create) {
            update() //cập nhật thay đổi
        }
        else{
            //create là để biết gọi từ hàm signUp
            //thì tạo trên firebase một document cho người dùng
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
        this.fullName = name
        this.email=email
        this.phoneNumber=phoneNumber
        this.address=address
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