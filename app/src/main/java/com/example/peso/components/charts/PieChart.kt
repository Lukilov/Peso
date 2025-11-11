package com.example.peso.components.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp

@Composable
fun PieChart(
    values: List<Double>,
    modifier: Modifier = Modifier
) {
    val data = values.filter { it > 0 }
    if (data.isEmpty()) return

    val sum = data.sum()
    val fractions = data.map { it / sum }

    // Barve iz teme (zunaj Canvas)
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.tertiaryContainer,
        MaterialTheme.colorScheme.secondaryContainer
    )

    Box(modifier = modifier.fillMaxWidth().height(180.dp)) {
        Canvas(modifier = Modifier.fillMaxWidth().height(180.dp)) {
            val diameter = size.minDimension
            val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
            val rect = Size(diameter, diameter)

            var startAngle = -90f
            fractions.forEachIndexed { i, f ->
                val sweep = (f * 360f).toFloat()
                drawArc(
                    color = colors[i % colors.size],
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = true,
                    topLeft = topLeft,
                    size = rect
                )
                startAngle += sweep
            }
        }
    }
}
