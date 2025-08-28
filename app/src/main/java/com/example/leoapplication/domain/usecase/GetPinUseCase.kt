package com.example.leoapplication.domain.usecase

import com.example.leoapplication.domain.repository.PinRepository
import javax.inject.Inject

class GetPinUseCase @Inject constructor(
    private val repository: PinRepository
) {
    fun execute(): String? = repository.getPin()
}
