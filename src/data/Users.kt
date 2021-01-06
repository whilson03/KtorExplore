package com.olabode.wilson.data
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

/** CREATED BY wilson ON 06  Jan 2021 @ 12:38 pm */

object Users : Table() {
    val userId : Column<Int> = integer("id").autoIncrement().primaryKey()
    val email = varchar("email", 128).uniqueIndex()
    val displayName = varchar("display_name", 256)
    val passwordHash = varchar("password_hash", 64)
}
