package com.blpw.pixelex.features.stackUserList.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.blpw.pixelex.features.stackUserList.data.local.dao.StackUserInfoDao
import com.blpw.pixelex.features.stackUserList.data.local.dao.UserRemoteKeysDao
import com.blpw.pixelex.features.stackUserList.data.local.entities.StackUserInfoEntity
import com.blpw.pixelex.features.stackUserList.data.local.entities.UserRemoteKeys
import com.blpw.pixelex.features.stackUserList.data.local.entities.FollowEntity

@Database(
    entities = [StackUserInfoEntity::class, FollowEntity::class, UserRemoteKeys::class],
    version = 1,
    exportSchema = true
)
abstract class StackUserDatabase : RoomDatabase() {
    abstract fun stackUserInfoDao(): StackUserInfoDao
    abstract fun userRemoteKeysDao(): UserRemoteKeysDao
}