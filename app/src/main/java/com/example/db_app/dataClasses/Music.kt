package com.example.db_app.dataClasses

data class Music(
    private val id: Int,
    private val name: String,
    private val year: Int,
    private val duration: Int,
    private val artists: List<Artist>,
    private val genres: List<Genre>
) {

    fun getId() = id
    fun getName() = name
    fun getYear() = year
    fun getDuration() = duration
    fun getArtists() = artists
    fun getGenres() = genres

    fun getAlbumName(): String {
        // TODO: 13.05.2021
        return "альбом"
    }

    fun getArtistString(): String {
        val artist = StringBuilder()
        artists.forEach {
            artist.append(it.getName())
            artist.append(", ")
        }
        artist.delete(artist.length - 2, artist.length - 1)

        return artist.toString()
    }

    fun getGenreSting(): String {
        val genre = StringBuilder()
        genres.forEach {
            genre.append(it.getName())
            genre.append(", ")
        }
        genre.delete(genre.length - 2, genre.length - 1)

        return genre.toString()
    }
}