/*
 * Copyright (c) 2021. Code by Juniell.
 */

package com.example.db_app.dataClasses

data class ContentIdName(
    private val id: Int,
    private val name: String
) {
    fun getId() = id
    fun getName() = name
}