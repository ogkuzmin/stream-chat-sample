package com.devundefined.streamchatsample.di

import android.content.Context
import android.content.SharedPreferences
import com.devundefined.streamchatsample.domain.UserRepo
import com.devundefined.streamchatsample.domain.UserRepoImpl
import com.devundefined.streamchatsample.infrastructure.KeyValueStorage
import com.devundefined.streamchatsample.infrastructure.KeyValueStorageImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object AppModule {

    val module = module {
        single<SharedPreferences> {
            androidContext().getSharedPreferences(
                "SHARED_PREFS_FILE",
                Context.MODE_PRIVATE
            )
        }

        single<KeyValueStorage> { KeyValueStorageImpl(get())}

        factory<UserRepo> { UserRepoImpl(get()) }
    }
}