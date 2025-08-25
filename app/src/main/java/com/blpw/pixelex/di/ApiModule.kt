package com.blpw.pixelex.di

import com.blpw.pixelex.features.stackUserList.data.network.StackUsersApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): StackUsersApiService =
        retrofit.create(StackUsersApiService::class.java)
}