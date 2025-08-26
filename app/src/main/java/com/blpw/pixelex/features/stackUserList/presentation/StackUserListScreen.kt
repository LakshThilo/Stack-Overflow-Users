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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.blpw.pixelex.common.data.LoadingStatus
import com.blpw.pixelex.common.util.backgroundPalette
import com.blpw.pixelex.features.stackUserList.presentation.components.ErrorScreen
import com.blpw.pixelex.features.stackUserList.presentation.components.SortChipsBar
import com.blpw.pixelex.features.stackUserList.presentation.components.StackedCardsList
import com.blpw.pixelex.features.stackUserList.presentation.util.StackUserSort
import com.blpw.pixelex.features.stackUserList.presentation.util.sortedByOption
import com.blpw.pixelex.navigation.LocalNavigationHelper
import com.blpw.pixelex.navigation.NavigationHelper
import com.blpw.pixelex.ui.theme.LightMustard
import com.blpw.pixelex.ui.theme.SandYellow
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
    val pagingItems = viewModel.usersPaging.collectAsLazyPagingItems()

    val refresh = pagingItems.loadState.refresh
    val isInitialLoading = pagingItems.itemCount == 0 && refresh is LoadState.Loading
    val isPullToRefresh = pagingItems.itemCount > 0 && refresh is LoadState.Loading

    val swipeState = rememberSwipeRefreshState(isPullToRefresh)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceBright)
            .padding(start = 16.dp, top = 54.dp, end = 16.dp)
    ) {
        Text(text = "Stack Overflow Users", style = MaterialTheme.typography.headlineMedium)

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
                // initial refresh states for first load
                when (refresh) {
                    is LoadState.Error -> if (pagingItems.itemCount == 0) {
                        FullScreenError(
                            message = refresh.error.localizedMessage ?: "Something went wrong.",
                            onRetry = { pagingItems.retry() }
                        )
                        return@SwipeRefresh
                    }
                    else -> Unit
                }

                if (isInitialLoading) {
                    LoadingIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 48.dp)
                    )
                } else {
                    // snapshot only as a fallback for null placeholders
                    val snapshotItems by remember(pagingItems.itemSnapshotList, sort) {
                        derivedStateOf { pagingItems.itemSnapshotList.items.sortedByOption(sort) }
                    }

                    StackedCardsList(
                        usersSorted = snapshotItems,
                        viewModel = viewModel,
                        colors = backgroundPalette
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
