package com.devundefined.streamchatsample.domain

import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.client.models.name
import java.util.*

object UserExtensions {
    private const val KEY_TOKEN = "token"
    fun createToken(): String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjoidGVzdGVyIn0.TFiykeeVhgTWpNw_WY1BH4VQCLmWZofm44kxDBnyb9s"
    fun createUser(name: String): User = User(UUID.randomUUID().toString()).apply {
        this.name = name
        this.token = createToken()
    }

    var User.token: String
        get() = extraData[KEY_TOKEN]?.let { it as? String } ?: ""
        set(value) {
            extraData[KEY_TOKEN] = value
        }
}