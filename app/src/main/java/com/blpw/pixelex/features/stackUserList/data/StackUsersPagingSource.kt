package com.blpw.pixelex.features.stackUserList.data

import androidx.paging.PagingSource
import com.blpw.pixelex.common.domain.Result
import androidx.paging.PagingState
import com.blpw.pixelex.features.stackUserList.data.dataMappers.toStackUserInfoModel
import com.blpw.pixelex.features.stackUserList.data.network.RemoteStackUsersDataSource
import com.blpw.pixelex.features.stackUserList.domain.StackUserInfoModel

class StackUsersPagingSource(
    private val remote: RemoteStackUsersDataSource,
    private val pageSize: Int,
    private val order: String,
    private val sort: String,
    private val site: String
) : PagingSource<Int, StackUserInfoModel>() {

    override fun getRefreshKey(state: PagingState<Int, StackUserInfoModel>): Int? {
        val anchor = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchor)
        return page?.prevKey?.plus(1) ?: page?.nextKey?.minus(1)
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StackUserInfoModel> {
        val page = params.key ?: 1
        return when (val result = remote.getStackUsersUsingDataSource(
            page = page,
            pageSize = params.loadSize.coerceAtMost(pageSize),
            order = order,
            sort = sort,
            site = site
        )) {
            is Result.Success -> {
                val dto = result.data
                val items = dto.items.map { it.toStackUserInfoModel() }
                val nextKey = if (dto.hasMore) page + 1 else null
                LoadResult.Page(
                    data = items,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = nextKey
                )
            }
            is Result.Error -> LoadResult.Error(Exception(result.error.toString()))
        }
    }
}