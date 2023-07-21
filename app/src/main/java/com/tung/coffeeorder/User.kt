package com.tung.coffeeorder

class User(fullName: String, phoneNumber: String, email: String, address: String) {
    private var fullName=fullName
    private var phoneNumber=phoneNumber
    private var email=email
    private var address=address

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

    private val reward= Reward()


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

    fun getName(): String{
        return fullName
    }

    fun getPhoneNumber(): String{
        return phoneNumber
    }

    fun getEmail(): String{
        return email
    }

    fun getAddress(): String{
        return address
    }
}