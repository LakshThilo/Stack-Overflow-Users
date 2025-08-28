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
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class StackUserInfoMediator @Inject constructor(
    private val api: StackUsersApiService,
    private val db: StackUserDatabase,
    // optionally pass sort/order/site to keep consistent with DAO ordering
    // private val sort: String = "reputation", private val order: String = "desc", private val site: String = "stackoverflow"
) : RemoteMediator<Int, StackUserJoin>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StackUserJoin>
    ): MediatorResult {
        return try {
            // 1) Decide which page to load
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKey = getRemoteKeyClosestToCurrentPosition(state)
                    // If remoteKey is null, we’re starting fresh at page 1
                    remoteKey?.nextKey?.minus(1) ?: 1
                }
                LoadType.PREPEND -> {
                    val remoteKey = getRemoteKeyForFirstItem(state)
                    val prevKey = remoteKey?.prevKey
                    if (prevKey == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    prevKey
                }
                LoadType.APPEND -> {
                    val remoteKey = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKey?.nextKey
                    if (nextKey == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    nextKey
                }
            }

            // 2) Fetch
            val pageSize = state.config.pageSize
            val response = api.getStackExchangeUsers(page = page, pageSize = pageSize)
            val items = response.items
            // Prefer API flag if present
            val endOfPagination = (response.hasMore == false) || items.isEmpty()

            // 3) Persist
            val entities = items.map { it.toStackUserInfoEntity() }
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.userRemoteKeysDao().clearKeys()
                    db.stackUserInfoDao().clearUsers()
                    // DO NOT clear follows
                }

                db.stackUserInfoDao().upsertUsers(entities)

                val keys = entities.map {
                    UserRemoteKeys(
                        userId = it.userId,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (endOfPagination) null else page + 1
                    )
                }
                db.userRemoteKeysDao().insertAll(keys)
            }

            MediatorResult.Success(endOfPaginationReached = endOfPagination)
        } catch (t: Throwable) {
            MediatorResult.Error(t)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StackUserJoin>): UserRemoteKeys? {
        // last page that has data -> last item
        val lastItem = state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
        return lastItem?.let { db.userRemoteKeysDao().remoteKeys(it.user.userId) }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StackUserJoin>): UserRemoteKeys? {
        val firstItem = state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
        return firstItem?.let { db.userRemoteKeysDao().remoteKeys(it.user.userId) }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StackUserJoin>): UserRemoteKeys? {
        val anchor = state.anchorPosition ?: return null
        val closest = state.closestItemToPosition(anchor) ?: return null
        return db.userRemoteKeysDao().remoteKeys(closest.user.userId)
    }
}