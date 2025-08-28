package com.blpw.pixelex.features.stackUserList.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.blpw.pixelex.features.stackUserList.data.local.entities.UserRemoteKeys

@Dao
interface UserRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(keys: List<UserRemoteKeys>)
    @Query("SELECT * FROM user_remote_keys WHERE userId = :id")
    suspend fun remoteKeys(id: Int): UserRemoteKeys?
    @Query("DELETE FROM user_remote_keys")
    suspend fun clearKeys()
}