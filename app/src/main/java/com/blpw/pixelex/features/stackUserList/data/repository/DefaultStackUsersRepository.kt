package com.blpw.pixelex.features.stackUserList.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.blpw.pixelex.features.stackUserList.data.mappers.toStackUserInfoModel
import com.blpw.pixelex.features.stackUserList.data.network.RemoteStackUsersDataSource
import com.blpw.pixelex.features.stackUserList.domain.StackUserInfoModel
import com.blpw.pixelex.features.stackUserList.domain.StackUserRepository
import com.blpw.pixelex.common.domain.DataError
import com.blpw.pixelex.common.domain.Result
import com.blpw.pixelex.common.domain.map
import com.blpw.pixelex.features.stackUserList.data.StackUsersPagingSource
import com.blpw.pixelex.features.stackUserList.data.local.StackUserDatabase
import com.blpw.pixelex.features.stackUserList.data.local.StackUserInfoMediator
import com.blpw.pixelex.features.stackUserList.data.local.entities.FollowEntity
import com.blpw.pixelex.features.stackUserList.data.local.entities.StackUserJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class DefaultStackUsersRepository @Inject constructor(
    private val remoteDataSource: RemoteStackUsersDataSource,
    private val mediator: StackUserInfoMediator,
    private val db: StackUserDatabase,
) : StackUserRepository {

    override suspend fun getStackUsers(): Result<List<StackUserInfoModel>, DataError.Remote> {
        return remoteDataSource.getStackUsers()
            .map { dto ->
                dto.items
                    .map {
                        it.toStackUserInfoModel()
                    }
            }
    }

    override fun getStackUsersUsingPaging(sort: String): Flow<PagingData<StackUserInfoModel>> {
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
                    sort = sort,
                    site = "stackoverflow"
                )
            }
        ).flow
    }

    override fun getStackUserFromRemote(): Flow<PagingData<StackUserInfoModel>> =
        Pager(
            config = PagingConfig(pageSize = 30, enablePlaceholders = false),
            remoteMediator = mediator,
            pagingSourceFactory = { db.stackUserInfoDao().pagingSource() }
        ).flow.map { paging -> paging.map { it.toDomain() } }

    override suspend fun setFollow(userId: Int, follow: Boolean) {
        val dao = db.stackUserInfoDao()
        if (follow) dao.follow(FollowEntity(userId)) else dao.unfollow(userId)
    }
}

fun StackUserJoin.toDomain() = StackUserInfoModel(
    userId = user.userId,
    displayName = user.displayName,
    profileImage = user.profileImage,
    reputation = user.reputation,
    location = user.location,
    link = user.link,
    websiteUrl = user.websiteUrl,
    gold = user.gold,
    silver = user.silver,
    bronze = user.bronze,
    isFollowed = isFollowed,
    accountId = user.accountId,
    userType = user.userType,
    acceptRate = user.acceptRate,
    creationDate = user.creationDate,
    isEmployee = user.isEmployee,
    lastAccessDate = user.lastAccessDate,
    lastModifiedDate = user.lastModifiedDate,
    reputationChangeDay = user.reputationChangeDay,
    reputationChangeMonth = user.reputationChangeMonth,
    reputationChangeQuarter = user.reputationChangeQuarter,
    reputationChangeWeek = user.reputationChangeWeek,
    reputationChangeYear = user.reputationChangeYear
)