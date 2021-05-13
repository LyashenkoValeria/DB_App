package com.example.db_app.dataClasses

data class Content(
    private val id: Int,
    private val name: String,
    private val rating: Double,
    private val author: String,
    private val poster: String
) {
    fun getId() = id
    fun getName() = name
    fun getAuthor() = author
    fun getRating() = rating
    fun getPoster() = poster
}