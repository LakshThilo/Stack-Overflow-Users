package com.blpw.pixelex.features.stackUserList.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.blpw.pixelex.features.stackUserList.domain.StackUserInfoModel
import com.blpw.pixelex.ui.theme.CardTextGreen
import com.blpw.pixelex.ui.theme.selectedMint

@Composable
fun FollowPill(
    user: StackUserInfoModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val followed = user.isFollowed

    // Smooth color transitions
    val bgColor by animateColorAsState(
        if (followed) selectedMint else Color.White.copy(alpha = 0.95f),
        label = "bgColor"
    )
    val borderColor by animateColorAsState(
        if (followed) Color.White else CardTextGreen,
        label = "borderColor"
    )
    val contentColor by animateColorAsState(
        if (followed) Color.White else CardTextGreen,
        label = "contentColor"
    )

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = bgColor,
        shadowElevation = 3.dp,
        border = BorderStroke(2.dp, borderColor),
        modifier = modifier
            .heightIn(min = 36.dp)
            .wrapContentWidth()            // keep the pill tight to content
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)  // single click target here
    ) {
        // center content; no "extra space" at the end
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AnimatedContent(
                targetState = followed,
                label = "followAnim"
            ) { isOn ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (isOn) "Following" else "Follow",
                        style = MaterialTheme.typography.labelLarge,
                        color = contentColor
                    )
                    Icon(
                        imageVector = if (isOn) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = if (isOn) "Following" else "Follow",
                        tint = contentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}