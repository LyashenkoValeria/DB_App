package com.example.db_app.dataClasses

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
    // TODO: 12.05.2021  Разобраться с группами и отдельными артистами
)