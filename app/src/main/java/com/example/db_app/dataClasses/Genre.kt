package com.example.db_app.dataClasses

data class Genre(
    private val id: Int,
    private val name: String,
    private val description: String
) {
    fun getId() = id
    fun getName() = name
    fun getDesc() = description
}