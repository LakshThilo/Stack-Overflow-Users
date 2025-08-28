package com.blpw.pixelex.features.stackUserList.domain

import androidx.paging.PagingData
import com.blpw.pixelex.common.domain.DataError
import com.blpw.pixelex.common.domain.Result
import kotlinx.coroutines.flow.Flow

interface StackUserRepository {
    // Remote
    suspend fun getStackUsers(): Result<List<StackUserInfoModel>, DataError.Remote>
    fun getStackUsersUsingPaging(sort: String = "reputation"): Flow<PagingData<StackUserInfoModel>>

    // Local
    fun getStackUserFromRemote(): Flow<PagingData<StackUserInfoModel>>
    suspend fun setFollow(userId: Int, follow: Boolean)
}