package com.blpw.pixelex.features.stackUserList.di

import com.blpw.pixelex.features.stackUserList.data.network.RemoteStackUsersDataSource
import com.blpw.pixelex.features.stackUserList.data.network.RemoteStackUsersDataSourceImpl
import com.blpw.pixelex.features.stackUserList.data.repository.DefaultStackUsersRepository
import com.blpw.pixelex.features.stackUserList.domain.StackUserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class StackUsersBindingsModule {

    @Binds
    abstract fun bindRemoteDataSource(
        impl: RemoteStackUsersDataSourceImpl
    ): RemoteStackUsersDataSource

    @Binds
    abstract fun bindRepository(
        impl: DefaultStackUsersRepository
    ): StackUserRepository
}