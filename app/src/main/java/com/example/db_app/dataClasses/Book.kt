package com.example.db_app.dataClasses

data class Book(
    private val id: Int,
    private val name: String,
    private val year: Int,
    private val description: String,
    private val poster: String?,
    private val rating: Double,
    private val bookSeries: BookSeries?,
    private val authors: List<People>,
    private val bookGenres: List<Genre>
) {

    fun getId() = id
    fun getName() = name
    fun getYear() = year
    fun getDesc() = description
    fun getPoster() = poster
    fun getBookSeries() = bookSeries
    fun getAuthors() = authors
    fun getGenres() = bookGenres
    fun getRating() = rating

    fun getAuthorsString() = authors.joinToString(separator = ", ")

    fun getGenreString() = bookGenres.joinToString(separator = ", ")
}