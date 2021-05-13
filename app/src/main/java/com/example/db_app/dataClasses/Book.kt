package com.example.db_app.dataClasses

data class Book(
    private val id: Int,
    private val name: String,
    private val year: Int,
    private val description: String,
    private val poster: String,
    private val bookSeries: BookSeries,
    private val peoples: List<People>,
    private val genres: List<Genre>
) {

    fun getId() = id
    fun getName() = name
    fun getYear() = year
    fun getDesc() = description
    fun getPoster() = poster
    fun getBookSeries() = bookSeries
    fun getPeoples() = peoples
    fun getGenres() = genres

    fun getPeopleString(): String {
        val author = StringBuilder()
        peoples.forEach {
            author.append(it.getFullName())
            author.append(", ")
        }
        author.delete(author.length - 2, author.length - 1)

        return author.toString()
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