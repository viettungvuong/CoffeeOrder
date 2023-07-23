package com.tung.coffeeorder

import android.location.Address

class User private constructor(){
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

    fun edit(name: String, email: String, phoneNumber: String, address: String){
        editEmail(email)
        editName(name)
        editPhoneNumber(phoneNumber)
        editAddress(address)
    }
}