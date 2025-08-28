package com.example.leoapplication.domain.repository

interface PinRepository {
    fun savePin(pin: String)
    fun getPin(): String?
}
