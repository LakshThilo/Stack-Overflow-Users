package com.blpw.pixelex.features.stackUserList.di

import android.content.Context
import androidx.room.Room
import com.blpw.pixelex.features.stackUserList.data.local.StackUserDatabase
import com.blpw.pixelex.features.stackUserList.data.local.dao.StackUserInfoDao
import com.blpw.pixelex.features.stackUserList.data.local.dao.UserRemoteKeysDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides @Singleton
    fun provideDatabase(@ApplicationContext context: Context): StackUserDatabase =
        Room.databaseBuilder(context, StackUserDatabase::class.java, "stack_user_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideStackUserInfoDao(db: StackUserDatabase): StackUserInfoDao = db.stackUserInfoDao()

    @Provides
    fun provideUserRemoteKeysDao(db: StackUserDatabase): UserRemoteKeysDao = db.userRemoteKeysDao()

}