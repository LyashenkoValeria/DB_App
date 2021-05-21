package com.example.db_app.dataClasses

data class Content(
    val id: Int,
    val name: String,
    val rating: Double,
    val year: Int,
    val genres: List<String>,
    val poster: String
) {
    fun getGenreString() = genres.joinToString(separator = ", ")
}


enum class Type(val t: Int) {
    BOOK(0), FILM(1), MUSIC(2)
}


data class ContentIdName(
    val id: Int,
    val name: String
) {
    fun getTopName(): String = name.split('(')[0]


    fun getTopAuthor(): String = name.split("- ")[1].dropLast(1)
}


data class Genre(
    val id: Int,
    val name: String,
    val description: String
)