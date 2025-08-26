package com.example.leoapplication.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
 @InstallIn(SingletonComponent::class)
 object AppModule {
//     @Provides
//     @Singleton
//     fun provideRetrofit(): Retrofit =
//         Retrofit.Builder()
//             .baseUrl("https://dummyjson.com/")
//             .addConverterFactory(GsonConverterFactory.create())
//             .build()
//
//     @Provides
//     @Singleton
//     fun provideUserApi(retrofit: Retrofit): UserApiService =
//         retrofit.create(UserApiService::class.java)
//
//     @Provides
//     @Singleton
//     fun provideUserRepository(api: UserApiService): UserRepository =
//         UserRepositoryImpl(api)
//
//     @Provides
//     @Singleton
//     fun provideGetUserUseCase(repository: UserRepository): GetUserUseCase =
//         GetUserUseCase(repository)
 }
