package com.blpw.pixelex.features.stackUserList.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.blpw.pixelex.features.stackUserList.domain.StackUserSort
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.blpw.pixelex.common.data.LoadingStatus
import com.blpw.pixelex.common.presentation.UiErrorModel
import com.blpw.pixelex.common.presentation.toUiError
import com.blpw.pixelex.features.stackUserList.presentation.StackUserState
import com.blpw.pixelex.features.stackUserList.presentation.UiErrorType
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    state: StackUserState,
    error: UiErrorType? = null,
    onRetry: (() -> Unit)? = null
) {
    val error = error.toUiError()
    var sort by rememberSaveable { mutableStateOf(StackUserSort.REPUTATION) }

    val isRefreshing = state.loadingStatus == LoadingStatus.Loading
    val swipeState = rememberSwipeRefreshState(isRefreshing)

    SwipeRefresh(
        state = swipeState,
        onRefresh = { onRetry?.invoke() },
        indicator = { s, trigger ->
            SwipeRefreshIndicator(
                state = s,
                refreshTriggerDistance = trigger
            )
        },
        modifier = modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, top = 84.dp, end = 16.dp)
        ) {
            Text("Stack Overflow Users", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(12.dp))
            SortChipsBar(selected = sort, onSelectedChange = { sort = it })
            Spacer(Modifier.height(32.dp))

            ErrorMessageSection(error)
        }
    }
}

@Composable
private fun ErrorMessageSection(error: UiErrorModel) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = error.icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = error.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        if (error.message.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = error.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}