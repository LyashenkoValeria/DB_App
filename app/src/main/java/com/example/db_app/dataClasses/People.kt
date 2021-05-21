package com.example.db_app.dataClasses

data class People(
    val id: Int,
    val fullname: String,
    val yearOfBirth: Int,
    val function: String?   // только для фильмов
)