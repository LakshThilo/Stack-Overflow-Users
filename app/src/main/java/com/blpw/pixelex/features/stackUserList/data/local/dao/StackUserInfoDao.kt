package com.blpw.pixelex.features.stackUserList.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.blpw.pixelex.features.stackUserList.data.local.entities.FollowEntity
import com.blpw.pixelex.features.stackUserList.data.local.entities.StackUserInfoEntity
import com.blpw.pixelex.features.stackUserList.data.local.entities.StackUserJoin

@Dao
interface StackUserInfoDao {
    // writes
    @Upsert
    suspend fun upsertUsers(items: List<StackUserInfoEntity>)

    @Query("DELETE FROM users")
    suspend fun clearUsers()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun follow(e: FollowEntity)

    @Query("DELETE FROM follows WHERE userId = :id")
    suspend fun unfollow(id: Int)

    @Query("SELECT COUNT(*) FROM users") suspend fun countUsers(): Int

    @Query("SELECT userId FROM follows")
    suspend fun getAllFollowsIds(): List<Int>

    // paging (ORDER BY whatever you need; example uses reputation)
    @Query("""
        SELECT u.*,
               CASE WHEN f.userId IS NULL THEN 0 ELSE 1 END AS isFollowed
        FROM users u
        LEFT JOIN follows f ON u.userId = f.userId
        ORDER BY u.reputation DESC
    """)
    fun pagingSource(): PagingSource<Int, StackUserJoin>
}