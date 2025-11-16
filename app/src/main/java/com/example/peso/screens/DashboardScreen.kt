@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.peso.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.peso.model.BudgetViewModel
import com.example.peso.model.Period
import com.example.peso.model.Transaction
import com.example.peso.model.format2
import com.example.peso.model.FakeData
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.YearMonth

// ---- barve
private val Accent = Color(0xFFB9A6FF)
private val AccentSoft = Color(0x33B9A6FF)
private val Good = Color(0xFF6DE3A3)
private val Warn = Color(0xFFFFC466)
private val Bad  = Color(0xFFE37A6D)

/* ---------- TOP-LEVEL: CategoryStat ---------- */
data class CategoryStat(
    val label: String,
    val value: BigDecimal,
    val percent: Int
)

@Composable
fun DashboardScreen(
    vm: BudgetViewModel,
    onAddClick: () -> Unit = {}
) {
    var period by remember { mutableStateOf(Period.MONTH) }

    // 1) časovno okno
    val today = LocalDate.now()
    val (from, to) = when (period) {
        Period.DAY -> today to today
        Period.WEEK -> {
            val start = today.minusDays(((today.dayOfWeek.value + 6) % 7).toLong()) // ponedeljek
            val end = start.plusDays(6)
            start to end
        }
        Period.MONTH -> {
            val ym = YearMonth.from(today)
            ym.atDay(1) to ym.atEndOfMonth()
        }
        Period.YEAR -> LocalDate.of(today.year, 1, 1) to LocalDate.of(today.year, 12, 31)
    }

    // 2) poraba iz FakeData (+ !isIncome)
    val allTx = FakeData.transactions
    val expensesInRange = remember(period, allTx) {
        allTx.filter { !it.isIncome && !it.date.isBefore(from) && !it.date.isAfter(to) }
    }

    val spent: BigDecimal = expensesInRange.fold(BigDecimal.ZERO) { acc, t -> acc + t.amount.abs() }
    val limit: BigDecimal = vm.monthlyLimit ?: BigDecimal("800.00")

    val progress = if (limit > BigDecimal.ZERO)
        spent.divide(limit, 4, RoundingMode.HALF_UP).toFloat().coerceIn(0f, 1f)
    else 0f

    val statusColor = when {
        progress < 0.50f -> Good
        progress < 0.85f -> Warn
        else -> Bad
    }
    val statusLabel = when (statusColor) {
        Good -> "Dobro"
        Warn -> "Zmerno"
        else -> "Slabo"
    }

    // 3) Top kategorije po type (varen null/blank -> "Drugo")
    val catStats: List<CategoryStat> = remember(period, expensesInRange) {
        val byType = expensesInRange.groupBy { ((it.type) ?: "").ifBlank { "Drugo" } }
        val totals = byType.map { (raw, list) ->
            val name = mapTypeToName(raw)
            val sum  = list.fold(BigDecimal.ZERO) { acc, t -> acc + t.amount.abs() }
            name to sum
        }.sortedByDescending { it.second }

        val totalAbs = totals.fold(BigDecimal.ZERO) { acc, p -> acc + p.second }
        totals.take(4).map { (label, value) ->
            val pct = if (totalAbs > BigDecimal.ZERO)
                value.multiply(BigDecimal(100)).divide(totalAbs, 0, RoundingMode.HALF_UP).toInt()
            else 0
            CategoryStat(label, value, pct)
        }
    }

    // 4) zadnje transakcije v obdobju
    val recent: List<Transaction> = remember(period, expensesInRange) {
        expensesInRange.sortedByDescending { it.date }.take(5)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("PE-SØ", fontWeight = FontWeight.Bold, fontSize = 22.sp) }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("Dodaj transakcijo") },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                onClick = onAddClick,
                containerColor = Accent
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            item {
                PeriodChips(
                    selected = period,
                    onSelect = { period = it }
                )
            }

            item {
                BudgetSummaryCard(
                    spent = spent,
                    limit = limit,
                    progress = progress,
                    status = statusLabel,
                    tint = statusColor
                )
            }

            item { TopCategoriesCard(catStats) }

            item { RecentTransactionsCard(recent) }
        }
    }
}

/* ---------------- UI kosi ---------------- */

@Composable
private fun PeriodChips(
    selected: Period,
    onSelect: (Period) -> Unit
) {
    val items = listOf(Period.DAY, Period.WEEK, Period.MONTH, Period.YEAR)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items.forEach { p ->
            FilterChip(
                selected = selected == p,
                onClick = { onSelect(p) },
                label = { Text(p.labelCap) },
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}

@Composable
private fun BudgetSummaryCard(
    spent: BigDecimal,
    limit: BigDecimal,
    progress: Float,
    status: String,
    tint: Color
) {
    val remaining = (limit - spent).coerceAtLeast(BigDecimal.ZERO)
    val pctText = String.format("%.1f%%", progress * 100f)

    Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Ta mesec", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text("€${spent.format2()}", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold))
                Text("€${limit.format2()}", color = Color.Gray)
            }

            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = tint,
                trackColor = AccentSoft
            )

            Spacer(Modifier.height(12.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Preostanek", color = Color.Gray, fontSize = 12.sp)
                    Text("€${remaining.format2()}", fontWeight = FontWeight.Medium)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(pctText, color = Color.Gray)
                    Surface(shape = RoundedCornerShape(20.dp), color = Color.Transparent, border = BorderStroke(1.dp, tint)) {
                        Text(status, color = tint, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun TopCategoriesCard(stats: List<CategoryStat>) {
    Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Največje kategorije", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(12.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    stats.forEachIndexed { idx, s ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(donutColor(idx))
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(s.label, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("${s.percent}%", color = Color.Gray, fontSize = 12.sp)
                                Spacer(Modifier.width(8.dp))
                                Text("€${s.value.format2()}")
                            }
                        }
                    }
                }

                Spacer(Modifier.width(16.dp))

                Box(modifier = Modifier.size(84.dp), contentAlignment = Alignment.Center) {
                    DonutChart(stats.map { it.percent })
                }
            }
        }
    }
}

@Composable
private fun DonutChart(segments: List<Int>) {
    Canvas(modifier = Modifier.size(84.dp)) {
        val stroke = 14f
        val rect = Rect(
            left = stroke / 2,
            top = stroke / 2,
            right = size.width - stroke / 2,
            bottom = size.height - stroke / 2
        )
        var start = -90f
        val total = segments.sum().coerceAtLeast(1)
        segments.forEachIndexed { i, p ->
            val sweep = 360f * (p / total.toFloat())
            drawArc(
                color = donutColor(i),
                startAngle = start,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(width = stroke),
                size = rect.size,
                topLeft = rect.topLeft
            )
            start += sweep
        }
    }
}

private fun donutColor(i: Int): Color = when (i % 4) {
    0 -> Color(0xFF8EE3C3)
    1 -> Color(0xFFB9A6FF)
    2 -> Color(0xFFEFB8C8)
    else -> Color(0xFF9AD0F5)
}

@Composable
private fun RecentTransactionsCard(items: List<Transaction>) {
    Card(shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Zadnje transakcije", style = MaterialTheme.typography.titleMedium)
                TextButton(onClick = { /* navigate to all */ }) { Text("Pokaži vse") }
            }
            Spacer(Modifier.height(6.dp))

            if (items.isEmpty()) {
                Text("Ni odhodkov v tem obdobju.", color = Color.Gray)
            } else {
                items.forEach { tx ->
                    TransactionRow(tx)
                    Divider(color = Color(0x22FFFFFF))
                }
            }
        }
    }
}

@Composable
private fun TransactionRow(tx: Transaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(tx.name, fontWeight = FontWeight.Medium)
            Text(tx.date.toString(), fontSize = 12.sp, color = Color.Gray)
        }
        val amt = tx.amount
        Text(text = "-€${amt.abs().format2()}", fontWeight = FontWeight.SemiBold)
    }
}

/* ------------ helpers for Period labels ------------ */
private val Period.labelCap: String
    get() = when (this) {
        Period.DAY -> "Dan"
        Period.WEEK -> "Teden"
        Period.MONTH -> "Mesec"
        Period.YEAR -> "Leto"
    }

/* ------------ mapiranje imen kategorij (varno) ------------ */
private fun mapTypeToName(type: String?): String = when ((type?.lowercase() ?: "drugo")) {
    "food", "groceries", "shopping", "hrana" -> "Hrana"
    "drink", "coffee", "kava", "pijača"      -> "Kava"
    "transport", "fuel", "gorivo", "prevoz"  -> "Prevoz"
    "entertainment", "party", "zabava"       -> "Zabava"
    "drugo"                                  -> "Drugo"
    else -> type!!.replaceFirstChar { it.uppercase() }
}
