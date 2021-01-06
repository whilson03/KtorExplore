package com.olabode.wilson.routes

import com.olabode.wilson.API_VERSION
import com.olabode.wilson.auth.JwtService
import com.olabode.wilson.auth.MySession
import com.olabode.wilson.repository.Repository
import io.ktor.application.application
import io.ktor.application.call
import io.ktor.application.log
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.locations.KtorExperimentalLocationsAPI
import io.ktor.locations.Location
import io.ktor.locations.post
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.sessions.sessions
import io.ktor.sessions.set

/** CREATED BY wilson ON 06  Jan 2021 @ 2:25 pm */

const val USERS = "$API_VERSION/users"
const val USER_LOGIN = "$USERS/login"
const val USER_CREATE = "$USERS/create"

@KtorExperimentalLocationsAPI
@Location(USER_LOGIN)
class UserLoginRoute

@KtorExperimentalLocationsAPI
@Location(USER_CREATE)
class UserCreateRoute

@KtorExperimentalLocationsAPI
fun Route.users(
    db: Repository,
    jwtService: JwtService,
    hashFunction: (String) -> String
) {
    post<UserCreateRoute> {
        val signupParameters = call.receive<Parameters>()

        val password = signupParameters["password"]
            ?: return@post call.respond(
                HttpStatusCode.Unauthorized, "Missing Fields"
            )

        val displayName = signupParameters["displayName"]
            ?: return@post call.respond(
                HttpStatusCode.Unauthorized, "Missing Fields"
            )

        val email = signupParameters["email"]
            ?: return@post call.respond(
                HttpStatusCode.Unauthorized, "Missing Fields"
            )

        val hash = hashFunction(password)

        try {
            val newUser = db.addUser(email, displayName, hash)
            newUser?.userId?.let {
                call.sessions.set(MySession(it))
                call.respondText(
                    jwtService.generateToken(newUser),
                    status = HttpStatusCode.Created
                )
            }
        } catch (e: Throwable) {
            application.log.error("Failed to register user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems creating User")
        }
    }


    post<UserLoginRoute> {
        val signInParameters = call.receive<Parameters>()
        val password = signInParameters["password"]
            ?: return@post call.respond(
                HttpStatusCode.Unauthorized, "Missing Fields"
            )
        val email = signInParameters["email"]
            ?: return@post call.respond(
                HttpStatusCode.Unauthorized, "Missing Fields"
            )
        val hash = hashFunction(password)
        try {
            val currentUser = db.findUserByEmail(email)
            currentUser?.userId?.let {
                if (currentUser.passwordHash == hash) {
                    call.sessions.set(MySession(it))
                    call.respondText(jwtService.generateToken(currentUser))
                } else {
                    call.respond(
                        HttpStatusCode.BadRequest, "Problems retrieving User"
                    )
                }
            }
        } catch (e: Throwable) {
            application.log.error("Failed to register user", e)
            call.respond(HttpStatusCode.BadRequest, "Problems retrieving User")
        }
    }
}

