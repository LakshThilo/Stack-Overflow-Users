package com.blpw.pixelex.features.stackUserList.presentation.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.blpw.pixelex.features.stackUserList.domain.StackUserInfoModel
import kotlin.math.max

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StackUserItemCard(
    user: StackUserInfoModel,
    modifier: Modifier = Modifier,
    topCorner: Dp = 24.dp,      // only top corners are rounded
    notchWidth: Dp = 24.dp,
    notchDepth: Dp = 24.dp,
    handleTopOffset: Dp = 6.dp, // fixed distance from top edge
    containerColor: Color,
    expanded: Boolean = false,
    onClick: () -> Unit = {}
) {
    val density = LocalDensity.current
    val r = with(density) { topCorner.toPx() }
    val nW = with(density) { notchWidth.toPx() }
    val nD = with(density) { notchDepth.toPx() }
    val handleTopPx = with(density) { handleTopOffset.toPx() }
    val handleH = with(density) { 4.dp.toPx() }
    val handleW = with(density) { 26.dp.toPx() }


    val shape = remember(r, nW, nD, expanded) {
        GenericShape { size, _ ->
            val w = size.width
            val h = size.height
            val cx = w / 2f

            moveTo(r, 0f)                        // A) start at top edge, r pixels from left
            quadraticTo(0f, 0f, 0f, r)  // B) top-left rounded corner to the left edge

            if (expanded) {
                lineTo(0f, h - r)
                quadraticTo(0f, h, r, h)          // bottom-left
                lineTo(w - r, h)
                quadraticTo(w, h, w, h - r)       // bottom-right
            } else {
                lineTo(0f, h) // C) left edge straight down to bottom-left
                lineTo(w, h)  // D) bottom edge straight to bottom-right
            }

            lineTo(w, r)                         // E) right edge straight up to near the top
            quadraticTo(w, 0f, w - r, 0f)  // F) top-right rounded corner back onto top edge

            lineTo(cx + nW, 0f)                  // G) move along top edge to the right lip of the notch
            quadraticTo(cx, nD, cx - nW, 0f) // H) draw a concave notch: control at (cx, nD)
            //  smooth U-shape between (cx+nW,0) and (cx-nW,0)

            lineTo(r, 0f)   // I) finish the top edge back toward the start
            close()
        }
    }

    // subtle scale & elevation when expanded
    val scale by animateFloatAsState(if (expanded) 1.02f else 1f, label = "scale")

    // Expand to exactly 2x height (180 -> 360) on click
    val baseHeight = 200.dp
    val targetHeight = if (expanded) 380.dp else baseHeight
    val height by animateDpAsState(targetHeight, label = "cardHeight")
    val elevation by animateDpAsState(if (expanded) 10.dp else 6.dp, label = "cardElevation")

    Surface(
        shape = shape,
        color = containerColor,
        tonalElevation = 0.dp,
        shadowElevation = elevation,
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clickable(onClick = onClick)
            .animateContentSize()        // <— smooth height changes
            .padding(2.dp)
            .fillMaxWidth()
            .height(height)     // collapsed min height
            .drawBehind {
                val cx = size.width / 2f
                val x = cx - handleW / 2f
                val y = max(1f, handleTopPx - handleH / 2f)
                drawRoundRect(
                    color = containerColor,
                    topLeft = Offset(x, y),
                    size = Size(handleW, handleH),
                    cornerRadius = CornerRadius(handleH / 2f, handleH / 2f)
                )
            }
    ) {
        Box(Modifier.fillMaxWidth()) {
            if(!expanded) {
                DottedRadialBackground(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(end = 28.dp),
                    dotColor = Color.Black.copy(alpha = 0.08f),
                    rings = 3,
                    dotsPerRing = 28,
                    centerBiasX = 0.90f,
                    centerBiasY = 0.30f
                )
            }
            AvatarImage(
                user,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 23.dp, top = 22.dp)
            )
            Column {
                StackUserInf(user)
                AnimatedContent(expanded, user)
            }
        }
    }
}
