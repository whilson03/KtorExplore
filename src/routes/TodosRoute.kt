package com.olabode.wilson.routes

import com.olabode.wilson.API_VERSION
import com.olabode.wilson.auth.MySession
import com.olabode.wilson.repository.Repository
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.auth.authenticate
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.locations.*
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.patch
import io.ktor.sessions.get
import io.ktor.sessions.sessions

/** CREATED BY wilson ON 06  Jan 2021 @ 3:00 pm */

const val TODOS = "$API_VERSION/todos"

@KtorExperimentalLocationsAPI
@Location(TODOS)
class TodoRoute {

    @Location("/{id}")
    data class Update(val parent: TodoRoute, val id: Int?)

    @Location("/{id}")
    data class Delete(val parent: TodoRoute, val id: Int?)
}

@KtorExperimentalLocationsAPI
fun Route.todos(db: Repository) {

    authenticate("jwt") {
        post<TodoRoute> {
            val todosParameters = call.receive<Parameters>()
            val todo = todosParameters["todo"]
                ?: return@post call.respond(
                    HttpStatusCode.BadRequest, "Missing Todo"
                )
            val done = todosParameters["done"] ?: "false"

            val user = call.sessions.get<MySession>()?.let {
                db.findUser(it.userId)
            }
            if (user == null) {
                call.respond(
                    HttpStatusCode.BadRequest, "Problems retrieving User"
                )
                return@post
            }

            try {
                val currentTodo = db.addTodo(
                    user.userId, todo, done.toBoolean()
                )
                currentTodo?.id?.let {
                    call.respond(HttpStatusCode.OK, currentTodo)
                }
            } catch (e: Throwable) {
                application.log.error("Failed to add todo", e)
                call.respond(HttpStatusCode.BadRequest, "Problems Saving Todo")
            }
        }

        get<TodoRoute> {
            val user = call.sessions.get<MySession>()?.let { db.findUser(it.userId) }
            if (user == null) {
                call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
                return@get
            }
            try {
                val todos = db.getTodos(user.userId)
                call.respond(todos)
            } catch (e: Throwable) {
                application.log.error("Failed to get Todos", e)
                call.respond(HttpStatusCode.BadRequest, "Problems getting Todos")
            }
        }

        patch<TodoRoute.Update> { updateRoute ->
            val todosParameters = call.receive<Parameters>()

            val id = updateRoute.id ?: return@patch call.respond(
                HttpStatusCode.BadRequest, "Missing id"
            )
            val todo = todosParameters["todo"]
                ?: return@patch call.respond(
                    HttpStatusCode.BadRequest, "Missing Todo"
                )
            val done = todosParameters["done"] ?: "false"

            val user = call.sessions.get<MySession>()?.let {
                db.findUser(it.userId)
            }
            if (user == null) {
                call.respond(
                    HttpStatusCode.BadRequest, "Problems retrieving User"
                )
                return@patch
            }

            try {
                val currentTodo = db.updateTodo(
                    user.userId, id, todo, done.toBoolean()
                )
                currentTodo?.id?.let {
                    call.respond(HttpStatusCode.OK, currentTodo)
                }
            } catch (e: Throwable) {
                application.log.error("Failed to update todo", e)
                call.respond(HttpStatusCode.BadRequest, "Problems Updating Todo")
            }
        }

        delete<TodoRoute.Delete> { deleteRoute ->

            val id = deleteRoute.id ?: return@delete call.respond(
                HttpStatusCode.BadRequest, "Missing id"
            )

            val user = call.sessions.get<MySession>()?.let {
                db.findUser(it.userId)
            }
            if (user == null) {
                call.respond(
                    HttpStatusCode.BadRequest, "Problems retrieving User"
                )
                return@delete
            }

            try {
                db.deleteTodo(user.userId, id)
                call.respondText(status = HttpStatusCode.OK, text = "Deleted Successfully")
            } catch (e: Throwable) {
                application.log.error("Failed to Delete todo", e)
                call.respond(HttpStatusCode.BadRequest, "Problems Deleting Todo")
            }
        }
    }
}
