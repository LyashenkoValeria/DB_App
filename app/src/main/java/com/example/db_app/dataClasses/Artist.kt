package com.example.db_app.dataClasses

data class Artist(
    private val id: Int,
    private val name: String,
    private val description: String
    // TODO: 12.05.2021  Разобраться с группами и отдельными артистами
) {
    fun getId() = id
    fun getName() = name
    fun getDesc() = description
}