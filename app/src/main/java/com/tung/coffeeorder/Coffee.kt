package com.tung.coffeeorder

import android.media.Image
import android.os.Parcel
import android.os.Parcelable

class Coffee(private val coffeeName: String, private val imageFilename: String, private val price: Long) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(coffeeName)
        parcel.writeString(imageFilename)
        parcel.writeLong(price)
    }

    override fun describeContents(): Int {
        return 0
    }

    fun getName(): String {
        return coffeeName
    }

    fun getImageFilename(): String {
        return imageFilename
    }

    fun getPrice(): Long {
        return price
    }

    companion object CREATOR : Parcelable.Creator<Coffee> {
        override fun createFromParcel(parcel: Parcel): Coffee {
            return Coffee(parcel)
        }

        override fun newArray(size: Int): Array<Coffee?> {
            return arrayOfNulls(size)
        }
    }
}
