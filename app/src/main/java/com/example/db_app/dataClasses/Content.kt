package com.example.db_app.dataClasses

import android.os.Parcel
import android.os.Parcelable

data class Content(
    val id: Int,
    val name: String,
    val rating: Double,
    val year: Int,
    val genres: List<String>,
    val poster: String
) {
    fun getGenreString() = genres.joinToString(separator = ", ")

    fun toContentIdName(): ContentIdName = ContentIdName(id, name)
}


enum class Type(val t: String) {
    BOOK("book"), FILM("film"), MUSIC("music")
//    BOOK(0), FILM(1), MUSIC(2)
}

enum class TypeLayout(val t: String) {
    LIST("list"), VIEWED("viewed"), RECOMMEND("recommend")
}


data class ContentIdName(
    val id: Int,
    val name: String
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ContentIdName> {
        override fun createFromParcel(parcel: Parcel): ContentIdName {
            return ContentIdName(parcel)
        }

        override fun newArray(size: Int): Array<ContentIdName?> {
            return arrayOfNulls(size)
        }
    }

    fun getTopName(): String = name.split('(')[0]

    fun getTopAuthor(): String = name.split("- ")[1].dropLast(1)
}


data class Genre(
    val id: Int,
    val name: String,
    val description: String
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()?: "",
        parcel.readString()?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Genre> {
        override fun createFromParcel(parcel: Parcel): Genre {
            return Genre(parcel)
        }

        override fun newArray(size: Int): Array<Genre?> {
            return arrayOfNulls(size)
        }
    }

}