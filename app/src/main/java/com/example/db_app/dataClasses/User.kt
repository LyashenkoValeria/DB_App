/*
 * Copyright (c) 2021. Code by Juniell.
 */

package com.example.db_app.dataClasses

import java.sql.Timestamp

data class User(
    val username: String,
    val email: String,
    val createTime: Timestamp
)