package com.example.leoapplication.di

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.leoapplication.data.repository.AuthRepositoryImpl
import com.example.leoapplication.data.repository.BankCardRepositoryImpl
import com.example.leoapplication.data.repository.PinRepositoryImpl
import com.example.leoapplication.data.repository.UserRepositoryImpl
import com.example.leoapplication.domain.repository.AuthRepository
import com.example.leoapplication.domain.repository.BankCardRepository
import com.example.leoapplication.domain.repository.PinRepository
import com.example.leoapplication.domain.repository.UserRepository
import com.example.leoapplication.domain.usecase.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

 // Firebase / Singleton-lər
 @Provides
 @Singleton
 fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

 @Provides
 @Singleton
 fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

 // Repository-lər
 @Provides
 @Singleton
 fun providePinRepository(@ApplicationContext context: Context): PinRepository =
  PinRepositoryImpl(context)

 @Provides
 @Singleton
 fun provideAuthRepository(auth: FirebaseAuth): AuthRepository =
  AuthRepositoryImpl(auth)


 @Provides
 @Singleton
 fun provideUserRepository(firestore: FirebaseFirestore): UserRepository =
  UserRepositoryImpl(firestore)

 @Provides
 @Singleton
 fun provideBankCardRepository(firestore: FirebaseFirestore): BankCardRepository =
  BankCardRepositoryImpl(firestore)

 // UseCase-lər
 @Provides
 @Singleton
 fun provideSavePinUseCase(repository: PinRepository): SavePinUseCase =
  SavePinUseCase(repository)

 @Provides
 @Singleton
 fun provideGetPinUseCase(repository: PinRepository): GetPinUseCase =
  GetPinUseCase(repository)


 @Provides
 fun provideGetUserUseCase(repository: UserRepository) = GetUserUseCase(repository)

 @Provides
 fun provideCreateUserUseCase(repository: UserRepository) = CreateUserUseCase(repository)

 @Provides
 fun provideAddCardToUserUseCase(repository: UserRepository) = AddCardToUserUseCase(repository)

 @Provides
 fun provideGetCardUseCase(repository: BankCardRepository) = GetCardUseCase(repository)

 @Provides
 fun provideCreateCardUseCase(repository: BankCardRepository) = CreateCardUseCase(repository)


 @Provides
 fun provideSendSmsUseCase(repository: AuthRepository): SendSmsUseCase =
  SendSmsUseCase(repository)

 @Provides
 fun provideVerifyCodeUseCase(repository: AuthRepository): VerifyCodeUseCase =
  VerifyCodeUseCase(repository)




}
