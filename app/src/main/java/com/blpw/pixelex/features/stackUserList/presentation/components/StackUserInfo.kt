package com.blpw.pixelex.features.stackUserList.presentation.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.blpw.pixelex.common.presentation.lastOnlineText
import com.blpw.pixelex.common.presentation.memberFor
import com.blpw.pixelex.features.stackUserList.domain.StackUserInfoModel
import com.blpw.pixelex.ui.theme.CardTextGreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StackUserInf(
    user: StackUserInfoModel,
    modifier: Modifier = Modifier,
    onFollowClick: () -> Unit = {}
) {
    Column(
        modifier = modifier.padding(start = 22.dp, top = 22.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = user.displayName,
                style = MaterialTheme.typography.titleLarge,
                color = CardTextGreen
            )
            if (user.userType == "registered") {
                Spacer(Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = "Verified",
                    tint = Color(0xFF1E88E5), // blue badge; tweak to match your palette
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.AccessTime,
                contentDescription = "Location",
                tint = CardTextGreen,
                modifier = Modifier.padding(end = 6.dp).size(16.dp)
            )
            Text(
                text = user.lastAccessDate.lastOnlineText(),
                style = MaterialTheme.typography.bodyMedium,
                color = CardTextGreen
            )
        }

        Spacer(Modifier.height(6.dp))
        Text(
            text = user.creationDate.memberFor(),
            style = MaterialTheme.typography.bodyMedium,
            color = CardTextGreen
        )
        Spacer(Modifier.height(6.dp))
        user.reputation?.let {
            Text(
                text = "$it",
                style = MaterialTheme.typography.bodyLarge,
                color = CardTextGreen
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            FollowPill(
                user,
                modifier = Modifier
                    .padding(end = 18.dp),
                onClick = onFollowClick
            )
        }
    }
}
