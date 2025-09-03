package com.example.leoapplication.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface CurrencyApi {
    @GET("latest/USD") // USD bazası
    suspend fun getRates(
        @Query("apikey") apiKey: String = "348c7d21b0fd7238cf7d85e9"
    ): CurrencyResponse
}
