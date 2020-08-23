package com.devundefined.streamchatsample.domain

import io.getstream.chat.android.client.models.User

object UserExtensions {
    fun createUser(name: String): User = User().apply { extraData["name"] = name }
}