package com.blpw.pixelex.features.stackUserList.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.blpw.pixelex.features.stackUserList.data.mappers.toDomain
import com.blpw.pixelex.features.stackUserList.data.network.RemoteStackUsersDataSource
import com.blpw.pixelex.features.stackUserList.domain.StackUserInfoModel
import com.blpw.pixelex.features.stackUserList.domain.StackUserRepository
import com.blpw.pixelex.common.domain.DataError
import com.blpw.pixelex.common.domain.Result
import com.blpw.pixelex.common.domain.map
import com.blpw.pixelex.features.stackUserList.data.StackUsersPagingSource
import kotlinx.coroutines.flow.Flow
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

    override fun getPagedUsers(sort: String): Flow<PagingData<StackUserInfoModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 30,
                prefetchDistance = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                StackUsersPagingSource(
                    remote = remoteDataSource,
                    pageSize = 30,
                    order = "desc",
                    sort = sort,               // "reputation" / "creation" / etc.
                    site = "stackoverflow"
                )
            }
        ).flow
    }
}