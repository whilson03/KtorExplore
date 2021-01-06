package com.olabode.wilson.repository

import com.olabode.wilson.models.User

/** CREATED BY wilson ON 06  Jan 2021 @ 12:50 pm */

interface Repository {

    suspend fun addUser(
        email: String,
        displayName: String,
        passwordHash: String
    ): User?

    suspend fun findUser(userId: Int): User?

    suspend fun findUserByEmail(email: String): User?
}