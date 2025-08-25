package com.blpw.pixelex.features.stackUserList.domain

import com.blpw.pixelex.common.domain.DataError
import com.blpw.pixelex.common.domain.Result

interface StackUserRepository {
    // Remote
    suspend fun getStackUsers(): Result<List<StackUserInfoModel>, DataError.Remote>

    // Local
}