package com.blpw.pixelex.features.stackUserList.data.repository

import com.blpw.pixelex.features.stackUserList.data.mappers.toDomain
import com.blpw.pixelex.features.stackUserList.data.network.RemoteStackUsersDataSource
import com.blpw.pixelex.features.stackUserList.domain.StackUserInfoModel
import com.blpw.pixelex.features.stackUserList.domain.StackUserRepository
import com.blpw.pixelex.common.domain.DataError
import com.blpw.pixelex.common.domain.Result
import com.blpw.pixelex.common.domain.map
import javax.inject.Inject

class DefaultStackUsersRepository @Inject constructor(
    private val remoteDataSource: RemoteStackUsersDataSource
) : StackUserRepository {

    override suspend fun getStackUsers(): Result<List<StackUserInfoModel>, DataError.Remote> {
        return remoteDataSource.getStackUsers()
            .map { dto ->
                dto.items
                    .map {
                        it.toDomain()
                    }
            }
    }
}