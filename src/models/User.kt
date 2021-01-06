package com.olabode.wilson.models

import io.ktor.auth.Principal
import java.io.Serializable

/** CREATED BY wilson ON 06  Jan 2021 @ 12:33 pm */

data class User(
    val userId: Int,
    val email: String,
    val displayName: String,
    val passwordHash: String
) : Serializable, Principal
