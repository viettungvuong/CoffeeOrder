package com.tung.coffeeorder

class User() {
    lateinit var fullName: String
    lateinit var phoneNumber: String
    lateinit var email: String
    lateinit var address: String

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