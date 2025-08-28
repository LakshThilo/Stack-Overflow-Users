package com.blpw.pixelex.features.stackUserList.data.local.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class StackUserInfoEntity(
    @PrimaryKey
    val userId: Int,
    val accountId: Int? = null,
    val displayName: String,
    val profileImage: String? = null,
    val reputation: Int,
    val location: String? = null,
    val userType: String,
    val link: String,
    val websiteUrl: String? = null,
    val acceptRate: Int? = null,
    val creationDate: Int,
    val isEmployee: Boolean,
    val lastAccessDate: Int,
    val lastModifiedDate: Int? = null,
    val reputationChangeDay: Int,
    val reputationChangeMonth: Int,
    val reputationChangeQuarter: Int,
    val reputationChangeWeek: Int,
    val reputationChangeYear: Int,
    val bronze: Int,
    val silver: Int,
    val gold: Int
)

@Entity(tableName = "follows")
data class FollowEntity(
    @PrimaryKey val userId: Int,
    val followedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_remote_keys")
data class UserRemoteKeys(
    @PrimaryKey val userId: Int,
    val prevKey: Int?,
    val nextKey: Int?
)

/** JOIN projection for paging that includes the boolean flag. */
data class StackUserJoin(
    @Embedded val user: StackUserInfoEntity,
    val isFollowed: Boolean
)