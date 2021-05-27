package com.example.db_app.dataClasses

data class Book(
    val id: Int,
    val name: String,
    val year: Int,
    val description: String,
    val poster: String?,
    val rating: Double,
    val bookSeries: ContentIdName?,
    val authors: List<People>,
    val genres: List<String>,
    val viewed: Boolean,
    val ratingUser: Double?,
    val top: ContentIdName?,
    val topPosition: Int?
) {
    fun getAuthorsString() = authors.joinToString(separator = ",\n")  { it.fullname }

    fun getGenreString() = genres.joinToString(separator = ", ")
}

//data class BookSeries(
//    val id: Int,
//    val name: String,
//    val description: String
//)