package com.example.leoapplication.domain.usecase

import com.example.leoapplication.data.remote.CurrencyResponse
import com.example.leoapplication.domain.repository.CurrencyRepository
import javax.inject.Inject

class GetCurrencyRatesUseCase @Inject constructor(
    private val repository: CurrencyRepository
) {
    suspend operator fun invoke(): CurrencyResponse = repository.getRates()
}