package com.blpw.pixelex.features.stackUserList.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.blpw.pixelex.features.stackUserList.presentation.util.StackUserSort

@Composable
fun SortChipsBar(
    selected: StackUserSort,
    onSelectedChange: (StackUserSort) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        StackUserSort.REPUTATION,
        StackUserSort.CREATION,
        StackUserSort.NAME,
        StackUserSort.MODIFIED
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { opt ->
            PillChip(
                text = opt.label,
                selected = opt == selected,
                onClick = { onSelectedChange(opt) }
            )
        }
    }
}

@Composable
fun PillChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = if (selected)
        MaterialTheme.colorScheme.background.copy(alpha = 0.10f)
    else Color.White

    val content = if (selected)
        MaterialTheme.colorScheme.primary
    else Color(0xFF1B1B1F)

    val borderColor = if (selected)
        MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
    else Color.Black.copy(alpha = 0.06f)

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = bg,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, borderColor),
        modifier = modifier
            .heightIn(min = 36.dp)
            .wrapContentWidth()
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text, color = content, style = MaterialTheme.typography.labelLarge)
        }
    }
}

