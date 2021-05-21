package com.example.db_app.dataClasses

import android.os.Parcel
import android.os.Parcelable

data class People(
    val id: Int,
    val fullname: String,
    val yearOfBirth: Int,
    val function: String?   // только для фильмов
): Parcelable {

    fun getId() = id
    fun getFullName() = fullName
    fun getOfBirth() = year
    fun getFunction() = function

    constructor(parcel: Parcel):this(
        parcel.readInt(),
        parcel.readString()?: "",
        parcel.readInt(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(fullName)
        parcel.writeInt(year)
        parcel.writeString(function)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<People> {
        override fun createFromParcel(parcel: Parcel): People {
            return People(parcel)
        }

        override fun newArray(size: Int): Array<People?> {
            return arrayOfNulls(size)
        }
    }
}