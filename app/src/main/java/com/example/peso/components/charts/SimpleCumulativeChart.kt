package com.example.peso.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SimpleCumulativeChart(
    dailyTotals: List<Double>,  // dolžina = dnevi v mesecu (manjkajoče zapolni z 0)
    limit: Double? = null
) {
    val height = 140.dp
    Canvas(Modifier.fillMaxWidth().height(height)) {
        if (dailyTotals.isEmpty()) return@Canvas
        val w = size.width
        val h = size.height

        // kumulativ
        val cum = dailyTotals.runningFold(0.0) { acc, v -> acc + v }.drop(1)
        val maxY = maxOf(cum.maxOrNull() ?: 0.0, limit ?: 0.0, 1.0)

        fun x(i: Int) = i * (w / (cum.size - 1).coerceAtLeast(1))
        fun y(v: Double) = h - (v / maxY * h).toFloat()

        // idealna diagonala
        drawLine(
            color = Color.LightGray,
            start = Offset(0f, y(0.0)),
            end = Offset(w, y((limit ?: cum.last()).toDouble())),
            strokeWidth = 2f
        )

        // črta limita (če obstaja)
        limit?.let {
            val ly = y(it)
            drawLine(
                color = Color(0xFFFF6B6B), // rdečkasta
                start = Offset(0f, ly),
                end = Offset(w, ly),
                strokeWidth = 2f
            )
        }

        // pot porabe
        for (i in 0 until cum.lastIndex) {
            drawLine(
                color = Color.White,
                start = Offset(x(i), y(cum[i])),
                end = Offset(x(i + 1), y(cum[i + 1])),
                strokeWidth = 4f
            )
        }
    }
}

private fun List<Double>.runningFold(init: Double, op: (Double, Double) -> Double): List<Double> {
    var acc = init
    return buildList {
        for (e in this@runningFold) {
            acc = op(acc, e)
            add(acc)
        }
    }
}
