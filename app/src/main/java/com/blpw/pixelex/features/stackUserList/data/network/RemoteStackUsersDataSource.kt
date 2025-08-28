package com.blpw.pixelex.features.stackUserList.data.network

import com.blpw.pixelex.features.stackUserList.data.dto.StackUserDto
import com.blpw.pixelex.common.domain.DataError
import com.blpw.pixelex.common.domain.Result

interface RemoteStackUsersDataSource {
    suspend fun getStackUsers(): Result<StackUserDto, DataError.Remote>
    suspend fun getStackUsersUsingDataSource(
        page: Int,
        pageSize: Int,
        order: String = "desc",
        sort: String = "reputation",
        site: String = "stackoverflow"
    ): Result<StackUserDto, DataError.Remote>
}