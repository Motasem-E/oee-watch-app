package com.example.oee.service

import com.example.oee.api.OeeInterface
import com.example.oee.repository.AppConfigurationsRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiService : KoinComponent {

    private val appConfigurations: AppConfigurationsRepository by inject()
    var instance: OeeInterface;

    init {
        instance = createInstance()
    }

    fun updateInstance(){
        instance = createInstance()
    }

    private fun createInstance(): OeeInterface {
        val retrofit = Retrofit.Builder()
            .baseUrl(appConfigurations.getMethod() + appConfigurations.getIp() + ":" + appConfigurations.getPort())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(OeeInterface::class.java)
    }
}