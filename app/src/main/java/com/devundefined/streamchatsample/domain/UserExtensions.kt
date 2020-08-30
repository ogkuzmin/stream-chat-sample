package com.devundefined.streamchatsample.domain

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.devundefined.streamchatsample.BuildConfig
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.client.models.name
import java.util.*

object UserExtensions {
    private const val KEY_TOKEN = "token"
    private fun createToken(id: String): String = generateToken(
        id,
        BuildConfig.API_SECRET
    )//"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoidGVzdGVyIn0.TFiykeeVhgTWpNw_WY1BH4VQCLmWZofm44kxDBnyb9s"

    fun createUser(name: String): User = User(UUID.randomUUID().toString()).apply {
        this.name = name
        this.token = createToken(id)
    }

    var User.token: String
        get() = extraData[KEY_TOKEN]?.let { it as? String } ?: ""
        set(value) {
            extraData[KEY_TOKEN] = value
        }

    private fun generateToken(userId: String, secretKey: String): String {
        return JWT.create()
            .withClaim("user_id", userId)
            .withHeader(
                mapOf(
                    "alg" to "HS256",
                    "typ" to "JWT"
                )
            )
            .sign(Algorithm.HMAC256(secretKey))
    }

    fun User.isEqualTo(another: User): Boolean = name == another.name && id == another.id
}