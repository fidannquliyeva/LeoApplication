package com.example.leoapplication.di

import com.example.leoapplication.data.repository.BankCardRepositoryImpl
import com.example.leoapplication.domain.repository.BankCardRepository
import com.example.leoapplication.domain.usecase.CreateCardUseCase
import com.example.leoapplication.domain.usecase.GetCardUseCase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

 @Provides
 @Singleton
 fun provideBankCardRepository(firestore: FirebaseFirestore): BankCardRepository {
  return BankCardRepositoryImpl(firestore)
 }

 @Provides
 @Singleton
 fun provideFirebaseFirestore(): FirebaseFirestore {
  return FirebaseFirestore.getInstance()
 }


 @Provides
 fun provideGetCardUseCase(repository: BankCardRepository) = GetCardUseCase(repository)

 @Provides
 fun provideCreateCardUseCase(repository: BankCardRepository) = CreateCardUseCase(repository)
}
