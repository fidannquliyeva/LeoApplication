package com.example.leoapplication.di

import PinRepositoryImpl
import com.example.leoapplication.domain.repository.AuthRepository
import com.example.leoapplication.data.repository.AuthRepositoryImpl
import com.example.leoapplication.data.repository.BankCardRepositoryImpl
import com.example.leoapplication.domain.repository.BankCardRepository
import com.example.leoapplication.domain.repository.PinRepository
import com.example.leoapplication.domain.usecase.CreateCardUseCase
import com.example.leoapplication.domain.usecase.GetCardUseCase
import com.example.leoapplication.domain.usecase.SendSmsUseCase
import com.example.leoapplication.domain.usecase.VerifyCodeUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
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

 @Provides
 @Singleton
 fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

 @Provides
 @Singleton
 fun provideAuthRepository(auth: FirebaseAuth): AuthRepository =
  AuthRepositoryImpl(auth)

 @Provides
 fun provideSendSmsUseCase(repository: AuthRepository) = SendSmsUseCase(repository)

 @Provides
 fun provideVerifyCodeUseCase(repository: AuthRepository) = VerifyCodeUseCase(repository)


}
