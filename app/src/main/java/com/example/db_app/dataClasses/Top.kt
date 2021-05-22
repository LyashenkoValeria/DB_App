/*
 * Copyright (c) 2021. Code by Juniell.
 */

package com.example.db_app.dataClasses

data class Top(
    val id: Int,
    val name: String,
    val content: List<TopEl>
) {
    private val nameTop = name.split('(')[0]
    private val authorTop = name.split("- ")[1].dropLast(1)

    fun getNameTop() = nameTop
    fun getAuthorTop() = authorTop
}

data class TopEl(
    val id: Int,
    val position: Int
)
