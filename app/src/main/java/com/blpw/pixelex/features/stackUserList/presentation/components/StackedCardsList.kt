package com.blpw.pixelex.features.stackUserList.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.blpw.pixelex.features.stackUserList.domain.StackUserInfoModel
import com.blpw.pixelex.features.stackUserList.presentation.StackUserState
import com.blpw.pixelex.features.stackUserList.presentation.StackUsersViewModel

@Composable
fun StackedCardsList(
    usersSorted: List<StackUserInfoModel>,
    state: StackUserState,
    viewModel: StackUsersViewModel,
    colors: List<Color>,
    overlap: Dp = 22.dp
) {
    var expandedId by rememberSaveable { mutableStateOf<Int?>(null) }

    LazyColumn(
        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(-overlap)
    ) {

        itemsIndexed(
            items = usersSorted,
            key = {  _, user -> user.userId }
        ) { index, user ->
            val expanded = expandedId == user.userId
            StackUserItemCard(
                user = user,
                containerColor = colors[index % colors.size],
                expanded = expanded,
                onClick = {
                    expandedId = if (expanded) null else user.userId
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(if (expanded) 100f else index.toFloat())
            )
        }
    }
}
