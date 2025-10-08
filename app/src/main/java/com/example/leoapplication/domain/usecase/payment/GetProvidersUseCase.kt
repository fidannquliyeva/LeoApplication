package com.example.leoapplication.domain.usecase.payment

import com.example.leoapplication.data.model.ServiceProvider
import com.example.leoapplication.domain.repository.PaymentRepository
import javax.inject.Inject

class GetProvidersUseCase @Inject constructor(
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(categoryId: String): Result<List<ServiceProvider>> {
        return repository.getProvidersByCategory(categoryId)
    }
}