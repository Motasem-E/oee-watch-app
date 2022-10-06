package com.example.oee

import com.example.oee.service.ApiService
import com.example.oee.repository.AppConfigurationsRepository
import com.example.oee.repository.DashboardRepository
import com.example.oee.service.WearableSyncService
import com.example.oee.viewmodel.DashboardViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val mainModule = module {

    single { ApiService() }

    single {
        AppConfigurationsRepository(this.androidContext())
    }

    single { DashboardRepository(get(), get()) }

    single { WearableSyncService(get(), get(), this.androidContext()) }

    single { DashboardViewModel(get(), get(), get()) }

}