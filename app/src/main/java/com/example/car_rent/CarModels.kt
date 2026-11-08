package com.example.car_rent

import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.core.os.ParcelCompat

data class Car (
    val id: Int,
    val name: String,
    val year: Int,
    val rating: Float,
    val kilometres: Int,
    val cost: Int,
    @DrawableRes val imageRes: Int,
    val imageCre: String = ""
) : Parcelable {

    constructor(parcel: Parcel) : this (
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readFloat(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeInt(year)
        parcel.writeFloat(rating)
        parcel.writeInt(kilometres)
        parcel.writeInt(cost)
        parcel.writeInt(imageRes)
        parcel.writeString(imageCre)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Car> {
        override fun createFromParcel(parcel: Parcel): Car = Car(parcel)
        override fun newArray(size: Int): Array<out Car?> = arrayOfNulls(size)
    }
}

data class Rental (
    val car: Car,
    val days: Int,
    val total: Int,
    val receiverName: String = ""
) : Parcelable {

    constructor(parcel: Parcel) : this (
        ParcelCompat.readParcelable(parcel, Car::class.java.classLoader, Car::class.java)!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(car, flags)
        parcel.writeInt(days)
        parcel.writeInt(total)
        parcel.writeString(receiverName)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Rental> {
        override fun createFromParcel(parcel: Parcel): Rental = Rental(parcel)
        override fun newArray(size: Int): Array<Rental?> = arrayOfNulls(size)
    }
}

object CarResp {
    fun getAllCars(): List<Car> = listOf(
        Car(1, "Brute Ambulance", 2013, 4.0f, 80_000, 120, R.drawable.amblc),
        Car(2, "Dinka Blista Compact", 2015, 4.2f, 120_000, 60, R.drawable.blistac),
        Car(3, "Pegassi Infernus", 2017, 4.8f, 250_000, 200, R.drawable.infernus),
        Car(4, "Dundreary Lanstalker", 2014, 4.5f, 105_000, 80, R.drawable.landstk),
        Car(5, "Benefactor Schafter", 2016, 4.9f, 180_000, 110, R.drawable.schaft)
    )
}