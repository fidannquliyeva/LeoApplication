package com.example.leoapplication.data.repository

import com.example.leoapplication.data.remote.CurrencyApi
import com.example.leoapplication.data.remote.CurrencyResponse
import com.example.leoapplication.domain.repository.CurrencyRepository
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(
    private val api: CurrencyApi
) : CurrencyRepository {
    override suspend fun getRates(): CurrencyResponse = api.getRates()
}