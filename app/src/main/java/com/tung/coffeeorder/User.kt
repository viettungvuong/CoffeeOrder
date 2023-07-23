package com.tung.coffeeorder

import android.location.Address
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
    }

    val loyalty=LoyaltyPoint()

    fun editName(newFullName: String){
        this.fullName=newFullName
    }

    fun editPhoneNumber(newPhoneNumber: String){
        this.phoneNumber=newPhoneNumber
    }

    fun editEmail(newEmail: String){
        this.email=newEmail
    }

    fun editAddress(newAddress: String){
        this.address=newAddress
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

    fun edit(id: String, name: String, email: String, phoneNumber: String, address: String){
        editEmail(email)
        editName(name)
        editPhoneNumber(phoneNumber)
        editAddress(address)

        this.id=id
    }

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
            db.collection("users").document(id).update(userData)
        } //dành cho những người không dùng firebase
        else{
            val editor = sharedPreferences.edit()
            editor.putString("email",email)
            editor.putString("name",fullName)
            editor.putString("phone-number",phoneNumber)
            editor.putString("address",address)
            editor.apply()
        }
    }

}