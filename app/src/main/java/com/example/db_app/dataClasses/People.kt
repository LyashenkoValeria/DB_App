package com.example.db_app.dataClasses

data class People(
    private val id: Int,
    private val fullName: String,
    private val year: Int,
    private val function: String?   // только для фильмов
) {

    fun getId() = id
    fun getFullName() = fullName
    fun getOfBirth() = year
    fun getFunction() = function
}