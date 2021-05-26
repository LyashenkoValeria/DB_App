package com.example.db_app.dataClasses

import android.os.Parcel
import android.os.Parcelable

data class Music(
    val id: Int,
    val name: String,
    val year: Int,
    val duration: Int,
    val poster: String?,
    val rating: Double,
    val artists: List<Artist>,
    val albums: List<MusicAlbum>,    // может быть пустым
    val genres: List<String>,
    val viewed: Boolean,
    val ratingUser: Double?,
    val top: ContentIdName?,
    val topPosition: Int?
) {

    fun getArtistString() = artists.joinToString(separator = ",\n") {it.name}

    fun getGenreString() = genres.joinToString(separator = ", ")

    fun getAlbumsString() = albums.joinToString(separator = ",\n") {it.name}
}


data class MusicAlbum(
    val id: Int,
    val name: String,
    val year: Int,
    val description: String?
)


data class Artist(
    val id: Int,
    val name: String,
    val description: String?
): Parcelable {
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(description)
    }

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()?: "",
        parcel.readString()?: ""
    )

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Artist> {
        override fun createFromParcel(parcel: Parcel): Artist {
            return Artist(parcel)
        }

        override fun newArray(size: Int): Array<Artist?> {
            return arrayOfNulls(size)
        }
    }

}