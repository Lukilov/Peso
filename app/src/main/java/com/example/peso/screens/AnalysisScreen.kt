package com.example.peso.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CarRental
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.LocalMall
import androidx.compose.material.icons.outlined.PartyMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.peso.model.FakeData
import com.example.peso.model.BudgetViewModel
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.roundToInt

// barve
private val Bg = Color(0xFF0F0E12)
private val Card = Color(0xFF17161B)
private val Muted = Color(0xFF8C8A95)
private val Accent = Color(0xFFB2A7FF)
private val AccentSoft = Color(0x33B2A7FF)
private val Bad = Color(0xFFE37A6D)
private val Good = Color(0xFF6DE3A3)

data class Stat(
    val title: String,
    val value: String,
    val badge: String? = null,
    val badgeTint: Color = Bad
)

data class Category(
    val icon: ImageVector,
    val name: String,
    val amount: Double,
    val deltaPct: Double? = null
)

enum class Period { DAY, WEEK, MONTH, YEAR }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(vm: BudgetViewModel) {
    val transactions = FakeData.transactions
    val expenses = transactions.filter { !it.isIncome } // samo odhodki

    var period by remember { mutableStateOf(Period.MONTH) }

    val now = LocalDate.now()
    val ym = YearMonth.of(now.year, now.month)
    val daysInMonth = ym.lengthOfMonth()

    // določi časovno obdobje glede na izbran period
    val (startDate, endDate) = when (period) {
        Period.DAY -> now to now
        Period.WEEK -> {
            val start = now.minusDays((now.dayOfWeek.value - 1).toLong()) // ponedeljek
            val end = start.plusDays(6)
            start to end
        }
        Period.MONTH -> {
            val start = ym.atDay(1)
            val end = ym.atEndOfMonth()
            start to end
        }
        Period.YEAR -> {
            val start = LocalDate.of(now.year, 1, 1)
            val end = LocalDate.of(now.year, 12, 31)
            start to end
        }
    }

    val periodExpenses = expenses.filter { it.date in startDate..endDate }

    // vsi dnevi v izbranem obdobju
    val daysInRange: List<LocalDate> = remember(startDate, endDate) {
        generateSequence(startDate) { d ->
            if (d.isBefore(endDate)) d.plusDays(1) else null
        }.toList()
    }

    // poraba po dnevih v izbranem obdobju
    val byDay: Map<LocalDate, Double> = periodExpenses
        .groupBy { it.date }
        .mapValues { (_, list) -> list.sumOf { it.amount.abs().toDouble() } }

    val points: List<Double> =
        if (daysInRange.isEmpty()) listOf(0.0)
        else daysInRange.map { d -> byDay[d] ?: 0.0 }

    val daysCount = daysInRange.size
    val total = periodExpenses.sumOf { it.amount.abs().toDouble() }
    val average = if (daysCount > 0) points.sum() / daysCount else 0.0

    // kategorije (varen handling za null/prazne tipe) – samo iz izbranega obdobja
    val categories = periodExpenses
        .groupBy { ((it.type) ?: "").ifBlank { "Drugo" } }
        .map { (type, list) ->
            val sum = list.sumOf { it.amount.abs().toDouble() }
            Category(
                icon = pickIcon(type),
                name = mapTypeToName(type),
                amount = sum
            )
        }
        .filter { it.name in listOf("Hrana", "Pijača", "Prevoz", "Zabava", "Drugo") }
        .sortedByDescending { it.amount }

    // --- LIMIT iz nastavitev ---
    val baseMonthlyLimit = (vm.monthlyLimit ?: BigDecimal("800.00")).toDouble()

    val limitGoal: Double = when (period) {
        Period.DAY -> baseMonthlyLimit / daysInMonth
        Period.WEEK -> baseMonthlyLimit / daysInMonth * 7.0
        Period.MONTH -> baseMonthlyLimit
        Period.YEAR -> baseMonthlyLimit * 12.0
    }

    val spent = categories.sumOf { it.amount }
    val progress = if (limitGoal > 0.0) (spent / limitGoal).coerceIn(0.0, 1.0) else 0.0
    val delta = total - average
    val deltaPct = if (average != 0.0) (delta / average * 100).roundToInt() else 0

    val hitDateText = "Poraba: €${format2(total)} / cilj €${format2(limitGoal)}"

    val periodLabel = when (period) {
        Period.DAY -> "izbran dan"
        Period.WEEK -> "izbran teden"
        Period.MONTH -> "izbran mesec"
        Period.YEAR -> "izbrano leto"
    }

    val insights = listOf(
        "Hrana predstavlja ${((categories.firstOrNull()?.amount ?: 0.0) / (spent.takeIf { it > 0 } ?: 1.0) * 100).roundToInt()} % vseh stroškov.",
        "Razlika do povprečja: ${if (delta >= 0) "+" else ""}${format2(delta)} / $deltaPct %.",
        "Poraba za $periodLabel temelji na ${periodExpenses.size} transakcijah."
    )

    Scaffold(
        containerColor = Bg,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Analiza", color = Color.White, fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = { /* back */ }) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Bg)
            )
        }
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                PeriodChips(
                    selected = period,
                    onSelect = { period = it }
                )
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    StatCard(
                        Stat("Skupaj", "€ ${format2(total)}"),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        Stat(
                            "Povprečje",
                            "€ ${format2(average)}",
                            badge = if (delta <= 0) "Dobro" else "Slabše",
                            badgeTint = if (delta <= 0) Good else Bad
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        Stat(
                            "Razlika do povprečja",
                            "${if (delta >= 0) "+" else ""}${format2(delta)} / $deltaPct %"
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                LimitCard(
                    title = "Limit",
                    subtitle = "${(progress * 100).roundToInt()} % porabe",
                    points = points,
                    goal = limitGoal,
                    spent = spent,
                    projectionText = hitDateText
                )
            }

            item { Text("Kategorije", color = Color.White, fontSize = 18.sp) }

            items(categories) { cat ->
                CategoryRow(cat, total = spent)
            }

            item { InsightsCard(insights) }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

/* ---------------- helperji za ikone/imenovanja (varni na null) ---------------- */

private fun pickIcon(type: String?): ImageVector = when ((type?.lowercase() ?: "drugo")) {
    "food", "hrana", "groceries", "shopping" -> Icons.Outlined.LocalMall
    "drink", "coffee", "kava", "pijača"      -> Icons.Outlined.Coffee
    "transport", "prevoz", "fuel", "gorivo"  -> Icons.Outlined.CarRental
    "entertainment", "zabava", "party"       -> Icons.Outlined.PartyMode
    else                                     -> Icons.Outlined.LocalMall
}

private fun mapTypeToName(type: String?): String = when ((type?.lowercase() ?: "drugo")) {
    "food", "groceries", "shopping", "hrana" -> "Hrana"
    "drink", "coffee", "kava", "pijača"      -> "Pijača"
    "transport", "fuel", "gorivo", "prevoz"  -> "Prevoz"
    "entertainment", "party", "zabava"       -> "Zabava"
    "drugo"                                  -> "Drugo"
    else                                     -> type!!.replaceFirstChar { it.uppercase() }
}

/* ---------------- UI deli ---------------- */

@Composable
private fun PeriodChips(
    selected: Period,
    onSelect: (Period) -> Unit
) {
    val items = listOf(
        Period.DAY to "Dan",
        Period.WEEK to "Teden",
        Period.MONTH to "Mesec",
        Period.YEAR to "Leto"
    )
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items.forEach { (p, label) ->
            val active = p == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(if (active) Accent else Card)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onSelect(p) }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(label, color = if (active) Bg else Color.White)
            }
        }
    }
}

@Composable
private fun StatCard(stat: Stat, modifier: Modifier = Modifier) {
    Surface(shape = RoundedCornerShape(20.dp), color = Card, modifier = modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(stat.title, color = Muted, fontSize = 13.sp)
            Spacer(Modifier.height(10.dp))
            Text(stat.value, color = Color.White)
            stat.badge?.let {
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(stat.badgeTint.copy(alpha = 0.18f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(it, color = stat.badgeTint, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun LimitCard(
    title: String,
    subtitle: String,
    points: List<Double>,
    goal: Double,
    spent: Double,
    projectionText: String
) {
    Surface(shape = RoundedCornerShape(20.dp), color = Card) {
        Column(Modifier.padding(16.dp)) {
            Text(title, color = Color.White)
            Spacer(Modifier.height(6.dp))
            Text(subtitle, color = Muted, fontSize = 13.sp)
            Spacer(Modifier.height(12.dp))
            TrendChart(points, 110.dp, 3.dp)
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(AccentSoft)
            ) {
                val pct = if (goal > 0.0) (spent / goal).coerceIn(0.0, 1.0).toFloat() else 0f
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(pct)
                        .background(Accent)
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Cilj: €${format2(goal)}", color = Muted, fontSize = 13.sp)
                Text("€ ${format2(spent)}", color = Color.White)
            }
            Spacer(Modifier.height(6.dp))
            Text(projectionText, color = Muted, fontSize = 12.sp)
        }
    }
}

@Composable
private fun TrendChart(
    points: List<Double>,
    height: Dp,
    stroke: Dp,
    goalLine: Double? = null
) {
    val data = if (points.isEmpty()) listOf(0.0) else points
    val min = data.minOrNull()!!
    val max = data.maxOrNull()!!
    val range = (max - min).takeIf { it != 0.0 } ?: 1.0

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1E1D23))
            .padding(12.dp)
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val step = w / (data.size - 1).coerceAtLeast(1)

            // fill
            val path = Path()
            data.forEachIndexed { i, v ->
                val x = i * step
                val y = h - ((v - min) / range).toFloat() * h
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            path.lineTo(w, h)
            path.lineTo(0f, h)
            path.close()
            drawPath(path, AccentSoft)

            // goal line (opcijsko)
            goalLine?.let { g ->
                val gy = h - ((g - min) / range).toFloat() * h
                drawLine(
                    Color(0x55FFFFFF),
                    start = Offset(0f, gy),
                    end = Offset(w, gy),
                    strokeWidth = 2f
                )
            }

            // line + markers
            var prev: Offset? = null
            data.forEachIndexed { i, v ->
                val x = i * step
                val y = h - ((v - min) / range).toFloat() * h
                val cur = Offset(x, y)
                prev?.let {
                    drawLine(
                        color = Accent,
                        start = it,
                        end = cur,
                        strokeWidth = stroke.toPx(),
                        cap = StrokeCap.Round
                    )
                }
                prev = cur
                drawCircle(Accent, radius = 4f, center = cur)
            }
        }
    }
}

@Composable
private fun CategoryRow(cat: Category, total: Double? = null) {
    val share = total?.takeIf { it > 0 }?.let { cat.amount / it } ?: 0.0
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Card,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF222129)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(cat.icon, contentDescription = cat.name, tint = Color(0xFFB9B7C3))
                }
                Spacer(Modifier.width(12.dp))
                Text(cat.name, color = Color.White, modifier = Modifier.weight(1f))
                Text("€ ${format2(cat.amount)}", color = Color.White)
            }
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(AccentSoft)
            ) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(share.toFloat().coerceIn(0f, 1f))
                        .background(Accent)
                )
            }
        }
    }
}

@Composable
private fun InsightsCard(lines: List<String>) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Card,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Vpogledi", color = Color.White)
            lines.forEach { Text("• $it", color = Muted, fontSize = 13.sp) }
        }
    }
}

private fun format2(x: Double): String = "%,.2f".format(x)
