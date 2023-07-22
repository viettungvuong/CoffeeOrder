package com.tung.coffeeorder

import android.location.Address

class User() {
    private lateinit var fullName: String
    private lateinit var phoneNumber: String
    private lateinit var email: String
    private lateinit var address: String

    inner class Reward(){
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
    }

    val reward= Reward()

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
}