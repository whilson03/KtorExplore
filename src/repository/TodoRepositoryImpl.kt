package com.olabode.wilson.repository

import com.olabode.wilson.data.DatabaseFactory.dbQuery
import com.olabode.wilson.data.Todos
import com.olabode.wilson.data.Users
import com.olabode.wilson.models.Todo
import com.olabode.wilson.models.User
import com.olabode.wilson.repository.Helpers.rowToTodo
import com.olabode.wilson.repository.Helpers.rowToUser
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.update

/** CREATED BY wilson ON 06  Jan 2021 @ 12:53 pm */

class TodoRepositoryImpl : Repository {

    override suspend fun addUser(email: String, displayName: String, passwordHash: String): User? {
        var statement: InsertStatement<Number>? = null
        dbQuery {

            statement = Users.insert { user ->
                user[Users.email] = email
                user[Users.displayName] = displayName
                user[Users.passwordHash] = passwordHash
            }
        }

        return rowToUser(statement?.resultedValues?.get(0))
    }

    override suspend fun findUser(userId: Int): User? = dbQuery {
        Users.select { Users.userId.eq(userId) }
            .map { rowToUser(it) }.singleOrNull()
    }

    override suspend fun findUserByEmail(email: String): User? = dbQuery {
        Users.select { Users.email.eq(email) }
            .map { rowToUser(it) }.singleOrNull()
    }

    override suspend fun addTodo(userId: Int, todo: String, done: Boolean): Todo? {
        var statement: InsertStatement<Number>? = null
        dbQuery {
            statement = Todos.insert {
                it[Todos.userId] = userId
                it[Todos.todo] = todo
                it[Todos.done] = done
            }
        }
        return rowToTodo(statement?.resultedValues?.get(0))
    }

    override suspend fun getTodos(userId: Int): List<Todo> {
        return dbQuery {
            Todos.select {
                Todos.userId.eq((userId))
            }.mapNotNull { rowToTodo(it) }
        }
    }

    override suspend fun updateTodo(userId: Int, todoId: Int, todo: String, done: Boolean): Todo? {
        return dbQuery {
            Todos.update({ (Todos.id eq todoId) and (Todos.userId eq userId) }) {
                it[Todos.todo] = todo
                it[Todos.done] = done
            }

            Todos.select {
                Todos.id.eq((todoId))
            }.map { rowToTodo(it) }.singleOrNull()
        }
    }
}