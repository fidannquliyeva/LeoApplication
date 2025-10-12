package com.example.leoapplication.di

import com.example.leoapplication.data.remote.FirestoreDataSource
import com.example.leoapplication.data.repository.HomeRepositoryImpl
import com.example.leoapplication.domain.repository.HomeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeModule {

    @Provides
    @Singleton
    fun provideHomeRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        firestoreDataSource: FirestoreDataSource
    ): HomeRepository {
        return HomeRepositoryImpl(firestore, auth, firestoreDataSource)
    }
}