package com.blpw.pixelex.features.stackUserList.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DottedRadialBackground(
    modifier: Modifier = Modifier,
    dotColor: Color = Color.Black.copy(alpha = 0.08f),
    rings: Int = 3,                 // how many concentric rings
    dotsPerRing: Int = 28,          // dots around each ring
    centerBiasX: Float = 0.80f,     // 0f..1f across the width
    centerBiasY: Float = 0.50f,     // 0f..1f down the height
    innerRadiusFactor: Float = 0.25f, // fraction of the card’s min dimension (0f..1f)
    dotRadius: Dp = 2.dp,           // size of each dot
    outerRadiusFactor: Float = 0.45f // how far the outer ring can go
) {
    Canvas(modifier) {
        val ringCount = rings.coerceAtLeast(1)
        val dots = dotsPerRing.coerceAtLeast(1)

        // clamp factors to sane ranges
        val outerF = outerRadiusFactor.coerceIn(0f, 1f)
        val innerF = innerRadiusFactor.coerceIn(0f, (outerF - 0.01f).coerceAtLeast(0f))

        val minDim = size.minDimension
        val maxR = minDim * outerF                 // outermost allowable radius
        val innerR = minDim * innerF               // first ring radius
        // step so last ring lands exactly at maxR (if ringCount > 1)
        val step = if (ringCount > 1) (maxR - innerR) / (ringCount - 1) else 0f

        val cx = size.width * centerBiasX.coerceIn(0f, 1f)
        val cy = size.height * centerBiasY.coerceIn(0f, 1f)
        val dotR = dotRadius.toPx()

        repeat(ringCount) { ring ->
            val radius = innerR + ring * step
            repeat(dots) { i ->
                val angle = (2 * Math.PI * i / dots).toFloat()
                val x = cx + radius * cos(angle)
                val y = cy + radius * sin(angle)
                drawCircle(dotColor, radius = dotR, center = Offset(x, y))
            }
        }
    }
}