package com.blpw.pixelex.features.stackUserList.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.blpw.pixelex.common.presentation.lastOnlineText
import com.blpw.pixelex.common.presentation.toDisplayUrl
import com.blpw.pixelex.features.stackUserList.domain.StackUserInfoModel
import com.blpw.pixelex.ui.theme.CardTextGreen

@Composable
fun AnimatedContent(expanded: Boolean, user: StackUserInfoModel) {
    val uriHandler = LocalUriHandler.current

    AnimatedVisibility(
        visible = expanded,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 28.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            AchievementsRow(
                gold = user.gold,
                silver = user.silver,
                bronze = user.bronze
            )
            Spacer(Modifier.height(8.dp))

            user.location?.takeIf { it.isNotBlank() }?.let { loc ->
                InfoRow(
                    icon = Icons.Default.LocationOn,
                    value = loc.toDisplayUrl()
                )
            }

            user.websiteUrl?.takeIf { it.isNotBlank() }?.let { url ->
                InfoRow(
                    icon = Icons.Outlined.Language,
                    value = url.toDisplayUrl(),
                    onClick = { runCatching { uriHandler.openUri(url) } }
                )
            }

            InfoRow(
                icon = Icons.Outlined.AccountCircle,
                value = user.link.toDisplayUrl(),
                onClick = { runCatching { uriHandler.openUri(user.link) } }
            )
        }
    }
}

@Composable
fun AchievementsRow(
    gold: Int,
    silver: Int,
    bronze: Int
) {
    Row(
        modifier = Modifier.padding(),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        BadgeStat(
            icon = Icons.Filled.WorkspacePremium,
            iconTint = Color(0xFFFFD54F),
            label = "Gold",
            count = gold,
            modifier = Modifier.weight(1f)
        )
        BadgeStat(
            icon = Icons.Filled.WorkspacePremium,
            iconTint = Color(0xFFB0BEC5),
            label = "Silver",
            count = silver,
            modifier = Modifier.weight(1f)
        )
        BadgeStat(
            icon = Icons.Filled.WorkspacePremium,
            iconTint = Color(0xFFBCAAA4),
            label = "Bronze",
            count = bronze,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun BadgeStat(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White.copy(alpha = 0.6f),
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 0.dp,
        border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.06f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 102.dp)   // optional: enforce consistent height
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .size(28.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFF1B1B1F)
            )
            Text(
                text = "$count",
                style = MaterialTheme.typography.labelLarge,
                color = Color(0xFF1B1B1F)
            )
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    value: String,
    onClick: (() -> Unit)? = null
) {
    Spacer(modifier = Modifier.height(4.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = CardTextGreen,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        val valueText = @Composable {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = CardTextGreen,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (onClick != null) {
            Box(Modifier.clickable(onClick = onClick)) { valueText() }
        } else {
            valueText()
        }
    }
}