package com.olabode.wilson.data

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

/** CREATED BY wilson ON 06  Jan 2021 @ 12:40 pm */

object Todos : Table() {
    val id: Column<Int> = integer("id").autoIncrement().primaryKey()
    val userId: Column<Int> = integer("userId").references(Users.userId)
    val todo = varchar("todo", 512)
    val done = bool("done")
}