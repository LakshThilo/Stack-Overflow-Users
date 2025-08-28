package com.blpw.pixelex.features.stackUserList.data.local

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.blpw.pixelex.features.stackUserList.data.local.entities.StackUserJoin
import com.blpw.pixelex.features.stackUserList.data.local.entities.UserRemoteKeys
import com.blpw.pixelex.features.stackUserList.data.mappers.toStackUserInfoEntity
import com.blpw.pixelex.features.stackUserList.data.network.StackUsersApiService
import java.io.IOException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class StackUserInfoMediator @Inject constructor(
    private val api: StackUsersApiService,
    private val db: StackUserDatabase
) : RemoteMediator<Int, StackUserJoin>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StackUserJoin>
    ): MediatorResult {
        return try {
            val wait = ApiBackoff.remainingMs()
            if (wait > 0) {
                kotlinx.coroutines.delay(wait)
            }

            val page = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKey = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKey?.nextKey?.minus(1) ?: 1
                }
                LoadType.PREPEND -> {
                    val remoteKey = getRemoteKeyForFirstItem(state)
                    val prevKey = remoteKey?.prevKey
                    if (prevKey == null) return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
                    prevKey
                }
                LoadType.APPEND -> {
                    val remoteKey = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKey?.nextKey
                    if (nextKey == null) return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
                    nextKey
                }
            }

            val pageSize = state.config.pageSize
            val response = api.getStackExchangeUsers(page = page, pageSize = pageSize)
            response.backoff?.let { ApiBackoff.setBackoffSeconds(it) }
            val users = response.items
            val end = !response.hasMore || response.items.isEmpty()

            val entities = users.map { userInfoDto -> userInfoDto.toStackUserInfoEntity() }

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.userRemoteKeysDao().clearKeys()
                    db.stackUserInfoDao().clearUsers()
                }

                db.stackUserInfoDao().upsertUsers(entities)

                val keys = entities.map {
                    UserRemoteKeys(
                        userId = it.userId,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (end) null else page + 1
                    )
                }
                db.userRemoteKeysDao().insertAll(keys)
            }

            MediatorResult.Success(endOfPaginationReached = end)
        } catch (e: retrofit2.HttpException) {
            val retryAfter = e.response()?.headers()?.get("Retry-After")?.toIntOrNull()
            if (retryAfter != null) ApiBackoff.setBackoffSeconds(retryAfter)
            return MediatorResult.Error(e)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (t: Throwable) {
            return MediatorResult.Error(t)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StackUserJoin>): UserRemoteKeys? {
        val last = state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull() ?: return null
        return db.userRemoteKeysDao().remoteKeys(last.user.userId)
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StackUserJoin>): UserRemoteKeys? {
        val first = state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull() ?: return null
        return db.userRemoteKeysDao().remoteKeys(first.user.userId)
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StackUserJoin>): UserRemoteKeys? {
        val anchor = state.anchorPosition ?: return null
        val closest = state.closestItemToPosition(anchor) ?: return null
        return db.userRemoteKeysDao().remoteKeys(closest.user.userId)
    }
}

