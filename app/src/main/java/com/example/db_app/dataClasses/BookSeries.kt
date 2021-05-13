package com.example.db_app.dataClasses

data class BookSeries(
    private val id: Int,
    private val name: String,
    private val description: String
) {
    fun getId() = id
    fun getName() = name
    fun getDesc() = description
}