package com.blpw.pixelex.features.stackUserList.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.blpw.pixelex.common.data.toUserMessage
import com.blpw.pixelex.features.stackUserList.presentation.components.SortChipsBar
import com.blpw.pixelex.features.stackUserList.presentation.components.StackedCardsList
import com.blpw.pixelex.navigation.LocalNavigationHelper
import com.blpw.pixelex.navigation.NavigationHelper
import com.blpw.pixelex.ui.theme.CardTextGreen
import com.blpw.pixelex.ui.theme.backgroundGreen
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun StackUserListScreen() {
    val viewModel: StackUsersViewModel = hiltViewModel()
    val navigationHelper = LocalNavigationHelper.current

    StackUserListScreenContent(viewModel, navigationHelper)
}

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    stroke: Dp = 5.dp
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.size(size),
            strokeWidth = stroke
        )
    }
}

@Composable
fun StackUserListScreenContent(
    viewModel: StackUsersViewModel,
    navigationHelper: NavigationHelper
) {
    val sort by viewModel.sort.collectAsState()
    val pagingItems = viewModel.usersPagingFromRemote.collectAsLazyPagingItems()

    val refresh = pagingItems.loadState.refresh
    val isInitialLoading = pagingItems.itemCount == 0 && refresh is LoadState.Loading
    val isPullToRefresh = pagingItems.itemCount > 0 && refresh is LoadState.Loading
    val swipeState = rememberSwipeRefreshState(isPullToRefresh)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGreen)
            .padding(start = 16.dp, top = 54.dp, end = 16.dp)
    ) {
        Text(
            "Stack Overflow Users",
            style = MaterialTheme.typography.headlineMedium,
            color = CardTextGreen
        )
        Spacer(Modifier.height(16.dp))

        SwipeRefresh(
            state = swipeState,
            onRefresh = { pagingItems.refresh() },
            indicator = { state, trigger ->
                 SwipeRefreshIndicator(state = state, refreshTriggerDistance = trigger)
            },
            modifier = Modifier.fillMaxSize()
        ) {
            Column {
                SortChipsBar(
                    selected = sort,
                    onSelectedChange = { viewModel.updateSort(it) }
                )

                when (refresh) {
                    is LoadState.Error -> if (pagingItems.itemCount == 0) {
                        FullScreenError(
                            message = refresh.error.toUserMessage(),
                            onRetry = { pagingItems.retry() }
                        )
                        return@SwipeRefresh
                    }
                    else -> Unit
                }

                if (isInitialLoading) {
//                    LoadingIndicator(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(top = 48.dp)
//                    )
                } else {
                    StackedCardsList(
                        pagingItems = pagingItems,
                        onFollowClick = { id, isFollowed ->
                            viewModel.onFollowClick(id, isFollowed)
                        }
                    )

                    if (pagingItems.itemCount == 0 && refresh is LoadState.NotLoading) {
                        EmptyState("No users found.")
                    }
                }
            }
        }
    }
}


@Composable
private fun FullScreenError(message: String, onRetry: () -> Unit) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(message, style = MaterialTheme.typography.bodyLarge)
        Spacer(Modifier.height(12.dp))
        Button(onClick = onRetry) { Text("Retry") }
    }
}

@Composable
fun PagingListFooterError(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = onRetry) { Text("Retry") }
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message, style = MaterialTheme.typography.bodyLarge)
    }
}
