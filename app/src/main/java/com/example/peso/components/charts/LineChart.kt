package com.example.peso.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

@Composable
fun LineChart(
    data: List<Float>,
    strokeColor: Color,
    modifier: Modifier = Modifier,
    gridLines: Int = 4
) {
    if (data.isEmpty()) return
    var touchX by remember { mutableStateOf<Float?>(null) }

    Canvas(
        modifier = modifier
            .pointerInput(data) {
                detectTapGestures(
                    onTap = { offset -> touchX = offset.x }
                )
            }
    ) {
        val w = size.width
        val h = size.height
        val left = 8f
        val right = w - 8f
        val top = 8f
        val bottom = h - 8f

        val minVal = data.minOrNull() ?: 0f
        val maxVal = data.maxOrNull() ?: 1f
        val span = max(1f, maxVal - minVal)

        // mreža
        repeat(gridLines) { i ->
            val y = top + (i / (gridLines - 1f)) * (bottom - top)
            drawLine(
                color = Color.White.copy(alpha = 0.08f),
                start = Offset(left, y),
                end = Offset(right, y),
                strokeWidth = 1f
            )
        }

        // pot + area
        val stepX = (right - left) / (data.size - 1).coerceAtLeast(1)
        val path = Path()
        val area = Path()

        data.forEachIndexed { idx, v ->
            val x = left + stepX * idx
            val norm = (v - minVal) / span
            val y = bottom - norm * (bottom - top)
            if (idx == 0) {
                path.moveTo(x, y)
                area.moveTo(x, bottom)
                area.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                area.lineTo(x, y)
            }
        }
        area.lineTo(right, bottom)
        area.close()

        // area fill
        drawPath(
            path = area,
            brush = Brush.verticalGradient(
                colors = listOf(strokeColor.copy(alpha = .18f), Color.Transparent)
            )
        )

        // črta
        drawPath(
            path = path,
            color = strokeColor,
            style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )

        // tooltip / dot
        touchX?.let { tx ->
            val clamped = min(max(tx, left), right)
            // najbližji indeks
            val idx = ((clamped - left) / stepX).toInt().coerceIn(0, data.size - 1)
            val v = data[idx]
            val norm = (v - minVal) / span
            val y = bottom - norm * (bottom - top)
            val x = left + stepX * idx

            // navpična vodilna
            drawLine(
                color = strokeColor.copy(alpha = .35f),
                start = Offset(x, top),
                end = Offset(x, bottom),
                strokeWidth = 2f
            )
            // pika
            drawCircle(
                color = strokeColor,
                radius = 6.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}
