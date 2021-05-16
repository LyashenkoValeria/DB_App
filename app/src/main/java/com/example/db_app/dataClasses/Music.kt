package com.example.db_app.dataClasses

data class Music(
    private val id: Int,
    private val name: String,
    private val year: Int,
    private val duration: Int,
    private val rating: Double,
    private val artists: List<Artist>,
    private val album: MusicAlbum?,
    private val genres: List<Genre>
) {

    fun getId() = id
    fun getName() = name
    fun getYear() = year
    fun getDuration() = duration
    fun getArtists() = artists
    fun getAlbum() = album
    fun getGenres() = genres
    fun getRating() = rating

    fun getArtistString() = artists.joinToString(separator = ", ")

    fun getGenreString() = genres.joinToString(separator = ", ")
}