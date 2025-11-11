package com.example.peso.screens



import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.peso.model.BudgetViewModel
import com.example.peso.model.Period
import java.math.BigDecimal
import kotlin.math.max
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    vm: BudgetViewModel,
    onSetLimitClick: () -> Unit
) {
    var period by remember { mutableStateOf(Period.MONTH) }

    // --- Safe VM reads (brez klicev neobstoječih funkcij) ---
    val total: BigDecimal? = runCatching { vm.totalFor(period) }.getOrNull()
    val income: BigDecimal? = runCatching { vm.incomeFor(period) }.getOrNull()
    val expense: BigDecimal? = runCatching { vm.expenseFor(period) }.getOrNull()
    val limit: BigDecimal? = runCatching { vm.monthlyLimit }.getOrNull() // property, ne funkcija
    val remaining: BigDecimal? = runCatching { vm.remainingAgainstLimit(period) }.getOrNull()
    val progress: Float = (runCatching { vm.budgetProgress(period) }.getOrNull() ?: 0f).coerceIn(0f, 1f)

    // Normaliziraj serijo na List<Float>
    val points: List<Float> = runCatching { vm.seriesFor(period) }.getOrNull()
        .let { series: Any? ->
            when (series) {
                is List<*> -> series.mapNotNull { item ->
                    when (item) {
                        is BigDecimal -> item.toFloat()
                        is Number -> item.toFloat()
                        is Pair<*, *> -> {
                            val y = item.second
                            when (y) {
                                is BigDecimal -> y.toFloat()
                                is Number -> y.toFloat()
                                else -> null
                            }
                        }
                        else -> null
                    }
                }
                else -> emptyList()
            }
        }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Analiza porabe") }) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PeriodSelector(selected = period, onSelect = { period = it })

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Skupaj", total.fmt(), modifier = Modifier.weight(1f))
                StatCard("Prihodki", income.fmt(), positive = true, modifier = Modifier.weight(1f))
                StatCard("Odhodki", expense.negFmt(), positive = false, modifier = Modifier.weight(1f))
            }

            DashboardCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Limit", style = MaterialTheme.typography.titleMedium)
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Mesečni limit", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(limit.fmt(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Preostanek", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            val rem = remaining ?: BigDecimal.ZERO
                            val over = rem < BigDecimal.ZERO
                            SmallPill(
                                text = if (over) "Presežek ${rem.abs().fmt()}" else rem.fmt(),
                                positive = !over
                            )
                        }
                    }
                    BudgetBar(progress = progress)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${(progress * 100).roundToInt()}% porabe", fontSize = 12.sp)
                        Text("Cilj: ${limit.fmt()}", fontSize = 12.sp)
                    }
                }
            }

            DashboardCard {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Trend", style = MaterialTheme.typography.titleMedium)
                    Sparkline(
                        points = points,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(96.dp)
                    )
                    val sum = points.fold(BigDecimal.ZERO) { acc, f -> acc + f.toBigDecimalSafe() }
                    Text(
                        "Skupaj v obdobju: ${sum.fmt()}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            OutlinedButton(onClick = onSetLimitClick, modifier = Modifier.align(Alignment.End)) {
                Text("Nastavi/uredi limit")
            }
        }
    }
}

/* ------------------------- UI helperji ------------------------- */

@Composable
private fun PeriodSelector(
    selected: Period,
    onSelect: (Period) -> Unit
) {
    val options = listOf(Period.DAY, Period.WEEK, Period.MONTH, Period.YEAR)

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (p in options) {
            val isSel = p == selected
            if (isSel) {
                Button(onClick = { onSelect(p) }) {
                    Text(p.label)
                }
            } else {
                OutlinedButton(onClick = { onSelect(p) }) {
                    Text(p.label)
                }
            }
        }
    }
}



@Composable
private fun StatCard(
    title: String,
    value: String,
    positive: Boolean? = null,
    modifier: Modifier = Modifier
) {
    DashboardCard(modifier) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            if (positive != null) {
                SmallPill(text = if (positive) "Dobro" else "Slabše", positive = positive)
            }
        }
    }
}

@Composable
private fun DashboardCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        shape = RoundedCornerShape(16.dp)
    ) { content() }
}

@Composable
private fun SmallPill(
    text: String,
    positive: Boolean
) {
    val bg = if (positive) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    else MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
    val fg = if (positive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text, color = fg, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun BudgetBar(progress: Float) {
    val clamped = progress.coerceIn(0f, 1f)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(10.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(999.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(clamped)
                .height(10.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(999.dp))
        )
    }
}

@Composable
private fun Sparkline(
    points: List<Float>,
    modifier: Modifier = Modifier
) {
    val colorPrimary = MaterialTheme.colorScheme.primary
    val colorFill = colorPrimary.copy(alpha = 0.12f)
    val colorText = MaterialTheme.colorScheme.onSurfaceVariant

    if (points.isEmpty()) {
        Box(modifier, contentAlignment = Alignment.Center) {
            Text("Ni podatkov", color = colorText, fontSize = 12.sp)
        }
        return
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val minY = points.minOrNull() ?: 0f
        val maxY = points.maxOrNull() ?: 1f
        val spanY = max(1e-3f, maxY - minY)
        val stepX = if (points.size > 1) w / (points.size - 1) else w

        val path = Path()
        points.forEachIndexed { idx, y ->
            val x = idx * stepX
            val normY = (y - minY) / spanY
            val yy = h - normY * h
            if (idx == 0) path.moveTo(x, yy) else path.lineTo(x, yy)
        }
        path.lineTo(w, h)
        path.lineTo(0f, h)
        path.close()

        // Uporabi barve, shranjene zunaj composable konteksta
        drawPath(path = path, color = colorFill)

        var prev: Offset? = null
        points.forEachIndexed { idx, y ->
            val x = idx * stepX
            val normY = (y - minY) / spanY
            val yy = h - normY * h
            val curr = Offset(x, yy)
            prev?.let {
                drawLine(
                    color = colorPrimary,
                    start = it,
                    end = curr,
                    strokeWidth = 3f
                )
            }
            prev = curr
        }
    }
}


/* ------------------------- util/formatting ------------------------- */

private fun BigDecimal?.fmt(): String =
    if (this == null) "—" else "€ " + this.toPlainString()

private fun BigDecimal?.negFmt(): String =
    if (this == null) "—" else "€ -" + this.abs().toPlainString()

private fun Float.toBigDecimalSafe(): BigDecimal =
    try { this.toString().toBigDecimal() } catch (_: Throwable) { BigDecimal.ZERO }

/* ------------------------- Period labels ------------------------- */

val Period.label: String
    get() = when (this) {
        Period.DAY -> "Dan"
        Period.WEEK -> "Teden"
        Period.MONTH -> "Mesec"
        Period.YEAR -> "Leto"
    }
