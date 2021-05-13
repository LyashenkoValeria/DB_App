package com.example.db_app.dataClasses

data class Content(
    private val id: Int,
    private val name: String,
    private val rating: Double,
    private val year: Int,
    private val genres: List<Genre>,
    private val poster: String
) {
    fun getId() = id
    fun getName() = name
    fun getYear() = year
    fun getRating() = rating
    fun getPoster() = poster

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