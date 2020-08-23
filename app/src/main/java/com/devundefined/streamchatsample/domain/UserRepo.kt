package com.devundefined.streamchatsample.domain

import com.devundefined.streamchatsample.infrastructure.KeyValueStorage
import io.getstream.chat.android.client.models.User

interface UserRepo {
    fun find(): User?
    fun save(user: User)
}

class UserRepoImpl(private val keyValueStorage: KeyValueStorage) : UserRepo {
    override fun find(): User? {
        return if (keyValueStorage.contains(KEY_USER_NAME)) {
            User().apply { extraData["name"] = keyValueStorage.get(KEY_USER_NAME, "") }
        } else {
            null
        }
    }

    override fun save(user: User) {
        keyValueStorage.save(KEY_USER_NAME, user.extraData["name"] as String)
    }

    companion object {
        private const val KEY_USER_NAME = "key_user_name"
    }
}