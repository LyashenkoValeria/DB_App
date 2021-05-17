/*
 * Copyright (c) 2021. Code by Juniell.
 */

package com.example.db_app.dataClasses

data class Top(
    private val id: Int,
    private val name: String,
    private val contentList: List<Content>
) {

    fun getId() = id
    fun getName() = name // TODO: 17.05.2021 Распарсить, чтобы name - чисто назвние, а getAuthor - автора.
    fun getAuthor() = "author"
    fun getContentList() = contentList

}