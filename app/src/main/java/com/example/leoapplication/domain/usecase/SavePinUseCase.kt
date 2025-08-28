package com.example.leoapplication.domain.usecase

import com.example.leoapplication.domain.repository.PinRepository
import javax.inject.Inject

class SavePinUseCase @Inject constructor(
    private val repository: PinRepository
) {
    fun execute(pin: String) {
        repository.savePin(pin)
    }
}
