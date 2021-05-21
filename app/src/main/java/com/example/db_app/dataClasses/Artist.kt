package com.example.db_app.dataClasses

import android.os.Parcel
import android.os.Parcelable
// TODO: перенсти изменения
//data class Artist(
//    private val id: Int,
//    private val name: String,
//    private val description: String
//    // TODO: 12.05.2021  Разобраться с группами и отдельными артистами
//): Parcelable {
//
//    fun getId() = id
//    fun getName() = name
//    fun getDesc() = description
//    override fun writeToParcel(parcel: Parcel, flags: Int) {
//        parcel.writeInt(id)
//        parcel.writeString(name)
//        parcel.writeString(description)
//    }
//
//    constructor(parcel: Parcel) : this(
//        parcel.readInt(),
//        parcel.readString()?: "",
//        parcel.readString()?: ""
//    )
//
//    override fun describeContents(): Int {
//        return 0
//    }
//
//    companion object CREATOR : Parcelable.Creator<Artist> {
//        override fun createFromParcel(parcel: Parcel): Artist {
//            return Artist(parcel)
//        }
//
//        override fun newArray(size: Int): Array<Artist?> {
//            return arrayOfNulls(size)
//        }
//    }
//}