package com.blpw.pixelex.features.stackUserList.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
    val state by viewModel.state.collectAsState()
    val navigationHelper = LocalNavigationHelper.current

    when(state.loadingStatus) {
        LoadingStatus.NotLoaded -> Unit
        LoadingStatus.Loading -> LoadingIndicator()
        LoadingStatus.Loaded -> StackUserListScreenContent(viewModel, state, navigationHelper)
        LoadingStatus.Error -> ErrorScreen(error = state.error, state = state, onRetry = {viewModel.retry()})
    }
}

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    stroke: Dp = 5.dp
){
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
    state: StackUserState,
    navigationHelper: NavigationHelper
) {
    var sort by rememberSaveable { mutableStateOf(StackUserSort.REPUTATION ) }
    val usersSorted by remember(state.users, sort) {
        derivedStateOf { state.users.sortedByOption(sort) }
    }

    val isRefreshing = state.loadingStatus == LoadingStatus.Loading
    val swipeState = rememberSwipeRefreshState(isRefreshing)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SandYellow.copy(alpha = 0.25f),
                        SandYellow.copy(alpha = 0.65f),
                        SandYellow,
                    )
                )
            )
            .padding(start = 16.dp, top = 84.dp, end = 16.dp)
    ) {
        Text(text = "Stack Overflow Users", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))
        SwipeRefresh(
            state = swipeState,
            onRefresh = {  viewModel.retry() },
            indicator = { state, trigger ->
                SwipeRefreshIndicator(
                    state = state,
                    refreshTriggerDistance = trigger
                )
            },
            modifier = Modifier.fillMaxSize()
        ) {
            Column {
                SortChipsBar(
                    selected = sort,
                    onSelectedChange = { sort = it }
                )
                StackedCardsList(
                    usersSorted,
                    state,
                    viewModel,
                    colors = backgroundPalette
                )
            }
        }
    }
}
