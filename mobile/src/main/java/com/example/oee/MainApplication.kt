package com.example.oee

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Dependeny Injection
        startKoin{
            androidContext(this@MainApplication)
            modules(mainModule)
        }
    }
}