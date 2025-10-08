package com.example.leoapplication.domain.repository

import com.example.leoapplication.data.model.Card
import com.example.leoapplication.data.model.User
import com.example.leoapplication.util.Resource
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    /**
     * İstifadəçi məlumatlarını almaq
     */
    suspend fun getUserData(): Resource<User>

    /**
     * İstifadəçinin kartlarını real-time izləmək
     */
    fun observeUserCards(): Flow<Resource<List<Card>>>

    /**
     * İstifadəçinin kartlarını bir dəfə almaq
     */
    suspend fun getUserCards(): Resource<List<Card>>
}