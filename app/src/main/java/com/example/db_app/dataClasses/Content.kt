package com.example.db_app.dataClasses

data class Content(
    private val id: Int,
    private val name: String,
    private val rating: Double,
    private val year: Int,
    private val genres: List<String>,
    private val poster: String
) {
    fun getId() = id
    fun getName() = name
    fun getYear() = year
    fun getRating() = rating
    fun getPoster() = poster

    fun getGenreString() = genres.joinToString(separator = ", ")
}

enum class Type(val t: Int) {
    BOOK(0), FILM(1), MUSIC(2)
}