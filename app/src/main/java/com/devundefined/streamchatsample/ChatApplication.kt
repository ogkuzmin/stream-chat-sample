package com.devundefined.streamchatsample

import android.app.Application
import com.devundefined.streamchatsample.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ChatApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(AppModule.module)
            androidContext(this@ChatApplication)
        }
    }
}