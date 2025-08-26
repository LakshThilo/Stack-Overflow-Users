package com.blpw.pixelex.features.stackUserList.data.network

import com.blpw.pixelex.common.data.safeApi
import com.blpw.pixelex.common.domain.DataError
import javax.inject.Inject
import com.blpw.pixelex.common.domain.Result
import com.blpw.pixelex.features.stackUserList.data.dto.StackUserDto
import com.squareup.moshi.Moshi

class RemoteStackUsersDataSourceImpl @Inject constructor(
    private val moshi: Moshi,
    private val api: StackUsersApiService
) : RemoteStackUsersDataSource {

    override suspend fun getStackUsers(): Result<StackUserDto, DataError.Remote> {
        return safeApi(moshi) {
            api.getStackExchangeUsers()
        }
    }

    override suspend fun getStackUsers(
        page: Int,
        pageSize: Int,
        order: String,
        sort: String,
        site: String
    ): Result<StackUserDto, DataError.Remote> {
        return safeApi(moshi) {
            api.getStackExchangeUsers(
                page = page,
                pageSize = pageSize,
                order = order,
                sort = sort,
                site = site
            )
        }
    }
}