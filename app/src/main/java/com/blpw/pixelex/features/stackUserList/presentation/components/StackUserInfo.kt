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
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.ui.zIndex
import com.blpw.pixelex.common.presentation.lastOnlineText
import com.blpw.pixelex.common.presentation.memberFor
import com.blpw.pixelex.features.stackUserList.domain.StackUserInfoModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StackUserInf(user: StackUserInfoModel, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(start = 22.dp, top = 22.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = user.displayName,
                style = MaterialTheme.typography.titleLarge,
                color = Color.Black
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
                tint = Color.Black.copy(alpha = 0.6f),
                modifier = Modifier.padding(end = 6.dp).size(16.dp)
            )
            Text(
                text = user.lastAccessDate.lastOnlineText(),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black.copy(alpha = 0.6f)
            )
        }

        Spacer(Modifier.height(6.dp))
        Text(
            text = user.creationDate.memberFor(),
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black.copy(alpha = 0.5f)
        )
        Spacer(Modifier.height(6.dp))
        user.reputation?.let {
            Text(
                text = "$it",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            FollowPill(
                text = "Follow",
                modifier = Modifier
                    .padding(end = 20.dp),
                onClick = { }
            )
        }
    }
}
