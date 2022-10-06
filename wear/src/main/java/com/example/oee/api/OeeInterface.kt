package com.example.oee.api

import com.example.oee.model.api.CellResponse
import com.example.oee.model.api.OeeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface OeeInterface {
    @GET("api/production/dashboard")
    fun getOEE(@Query("cell_id") cellId : String): Call<OeeResponse>

    @GET("api/cells")
    fun getCells(): Call<ArrayList<CellResponse>>
}