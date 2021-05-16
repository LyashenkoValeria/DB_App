package com.example.db_app.dataClasses

data class Film(
    private val id: Int,
    private val name: String,
    private val year: Int,
    private val duration: Int,
    private val description: String,
    private val poster: String?,
    private val rating: Double,
    private val filmSeries: FilmSeries?,
    private val book: Book?,
    private val music: List<Music>,   // TODO: 13.05.2021 надо это вообще?
    private val peoples: List<People>,
    private val genres: List<Genre>
) {

    fun getId() = id
    fun getName() = name
    fun getYear() = year
    fun getDuration() = duration
    fun getDesc() = description
    fun getPoster() = poster
    fun getFilmSeries() = filmSeries
    fun getBook() = book
    fun getMusic() = music
    fun getPeoples() = peoples
    fun getGenres() = genres
    fun getRating() = rating

    fun getBookName(): String? = book?.getName()

    fun getMakerString(): String {
        // TODO: 13.05.2021
        return "съёмочная группа"
    }

    fun getActorsString(): String {
        // TODO: 13.05.2021
        return "актёры"
    }

    fun getGenreString() = genres.joinToString(separator = ", ")
}