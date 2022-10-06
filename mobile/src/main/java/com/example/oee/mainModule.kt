package com.example.oee

import com.example.oee.model.AppConfigurations
import com.example.oee.service.WearableSyncService
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val mainModule = module {

    single { AppConfigurations() }

    single { WearableSyncService(get() , this.androidContext()) }

}