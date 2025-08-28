package com.blpw.pixelex.features.stackUserList.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.blpw.pixelex.features.stackUserList.domain.StackUserInfoModel
import com.blpw.pixelex.ui.theme.CardTextGreen

@Composable
fun FollowPill(
    user: StackUserInfoModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (user.isFollowed) Color.Gray else Color.White.copy(alpha = 0.95f),
        shadowElevation = 3.dp,
        border = BorderStroke(2.dp, CardTextGreen),
        modifier = modifier
            .heightIn(min = 36.dp)
            .wrapContentWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Follow",
                style = MaterialTheme.typography.labelLarge,
                color = CardTextGreen
            )
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = CardTextGreen,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}