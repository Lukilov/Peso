package com.example.peso.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.max

/**
 * Preprost stolpčni graf:
 * @param data seznam parov (label, value)
 * @param maxValue če je null, vzame maximum iz data
 */
@Composable
fun BarChart(
    data: List<Pair<String, Double>>,
    maxValue: Double? = null
) {
    if (data.isEmpty()) return
    Canvas(Modifier.fillMaxWidth().height(140.dp)) {
        val w = size.width
        val h = size.height
        val n = data.size
        val barW = w / (n * 2f)
        val gap = barW

        val m = max(maxValue ?: (data.maxOf { it.second }), 1.0)
        fun y(v: Double) = h - (v / m * h).toFloat()

        data.forEachIndexed { i, item ->
            val x = gap/2 + i * (barW + gap)
            val top = y(item.second)
            // bar
            drawLine(
                color = Color(0xFF9FA8DA),
                start = Offset(x + barW/2, h),
                end   = Offset(x + barW/2, top),
                strokeWidth = barW
            )
        }
    }
}
