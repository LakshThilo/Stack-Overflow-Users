package com.blpw.pixelex.features.stackUserList.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.blpw.pixelex.features.stackUserList.domain.StackUserInfoModel
import com.blpw.pixelex.features.stackUserList.presentation.PagingListFooterError

@Composable
fun StackedCardsList(
    pagingItems: LazyPagingItems<StackUserInfoModel>,
    overlap: Dp = 22.dp,
    onFollowClick: (userId: Int, isCurrentlyFollowed: Boolean) -> Unit
) {
    var expandedId by rememberSaveable { mutableStateOf<Int?>(null) }

    LazyColumn(
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(-overlap)
    ) {
        items(
            count = pagingItems.itemCount,
            key = { idx -> pagingItems[idx]?.userId ?: idx }
        ) { index ->
            val user = pagingItems[index] ?: return@items

            val expanded = expandedId == user.userId
            StackUserItemCard(
                user = user,
                expanded = expanded,
                onStackItemClick = { expandedId = if (expanded) null else user.userId },
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(if (expanded) 100f else index.toFloat()),
                onFollowClick = { onFollowClick(user.userId, user.isFollowed) }
            )
        }
        when (val append = pagingItems.loadState.append) {
            is LoadState.Loading -> item { PagingListFooterLoading() }
            is LoadState.Error -> item {
                PagingListFooterError(
                    message = append.error.localizedMessage ?: "Couldn't load more.",
                    onRetry = { pagingItems.retry() }
                )
            }
            else -> Unit
        }
    }
}

@Composable
fun PagingListFooterLoading() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

