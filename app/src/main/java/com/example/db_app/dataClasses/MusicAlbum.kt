package com.example.db_app.dataClasses

data class MusicAlbum(
    private val id: Int,
    private val name: String,
    private val year: Int,
    private val description: String
) {
    fun getId() = id
    fun getName() = name
    fun getYear() = year
    fun getDesc() = description
}