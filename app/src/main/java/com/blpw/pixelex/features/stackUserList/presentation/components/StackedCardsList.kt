package com.blpw.pixelex.features.stackUserList.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.blpw.pixelex.features.stackUserList.domain.StackUserInfoModel
import com.blpw.pixelex.features.stackUserList.presentation.PagingListFooterError
import com.blpw.pixelex.features.stackUserList.presentation.PagingListFooterLoading
import com.blpw.pixelex.features.stackUserList.presentation.StackUsersViewModel

@Composable
fun StackedCardsList(
    usersSorted: List<StackUserInfoModel>,
    viewModel: StackUsersViewModel,
    colors: List<Color>,
    overlap: Dp = 22.dp
) {
    var expandedId by rememberSaveable { mutableStateOf<Int?>(null) }
    val pagingItems = viewModel.usersPaging.collectAsLazyPagingItems()

    LazyColumn(
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(-overlap)
    ) {
        items(
            count = pagingItems.itemCount,
            key = { index ->
                pagingItems[index]?.userId
                    ?: usersSorted.getOrNull(index)?.userId
                    ?: index
            }
        ) { index ->
            val user = pagingItems[index] ?: usersSorted.getOrNull(index) ?: return@items

            val expanded = expandedId == user.userId
            StackUserItemCard(
                user = user,
                containerColor = colors[index % colors.size],
                expanded = expanded,
                onClick = { expandedId = if (expanded) null else user.userId },
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(if (expanded) 100f else index.toFloat())
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
