package com.olabode.wilson.models

/** CREATED BY wilson ON 06  Jan 2021 @ 12:35 pm */

data class Todo(
    val id: Int,
    val userId: Int,
    val todo: String,
    val done: Boolean
)
