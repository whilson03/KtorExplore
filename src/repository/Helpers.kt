package com.olabode.wilson.repository

import com.olabode.wilson.data.Todos
import com.olabode.wilson.data.Users
import com.olabode.wilson.models.Todo
import com.olabode.wilson.models.User
import org.jetbrains.exposed.sql.ResultRow

/** CREATED BY wilson ON 06  Jan 2021 @ 2:52 pm */
object Helpers {
    fun rowToUser(row: ResultRow?): User? {
        if (row == null) {
            return null
        }
        return User(
            userId = row[Users.userId],
            email = row[Users.email],
            displayName = row[Users.displayName],
            passwordHash = row[Users.passwordHash]
        )
    }

    fun rowToTodo(row: ResultRow?): Todo? {
        if (row == null) {
            return null
        }
        return Todo(
            id = row[Todos.id],
            userId = row[Todos.userId],
            todo = row[Todos.todo],
            done = row[Todos.done]
        )
    }
}