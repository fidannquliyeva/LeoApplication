package com.example.leoapplication.domain.repository

import com.example.leoapplication.data.remote.CurrencyResponse

interface CurrencyRepository {
    suspend fun getRates(): CurrencyResponse
}
