package com.devundefined.streamchatsample.domain

import com.devundefined.streamchatsample.domain.UserExtensions.token
import com.devundefined.streamchatsample.infrastructure.KeyValueStorage
import io.getstream.chat.android.client.models.User
import io.getstream.chat.android.client.models.name

interface UserRepo {
    fun find(): User?
    fun save(user: User)
}

class UserRepoImpl(private val keyValueStorage: KeyValueStorage) : UserRepo {
    override fun find(): User? {
        return if (keyValueStorage.contains(KEY_USER_ID)) {
            User(keyValueStorage.get(KEY_USER_ID, "")).apply {
                name = keyValueStorage.get(KEY_USER_NAME, "")
                token = keyValueStorage.get(KEY_USER_TOKEN, "")
            }
        } else {
            null
        }
    }

    override fun save(user: User) {
        keyValueStorage.save(KEY_USER_ID, user.id)
        keyValueStorage.save(KEY_USER_NAME, user.name)
        keyValueStorage.save(KEY_USER_TOKEN, user.token)
    }

    companion object {
        private const val KEY_USER_ID = "key_user_id"
        private const val KEY_USER_NAME = "key_user_name"
        private const val KEY_USER_TOKEN = "key_user_token"
    }
}