//package com.example.peso.screens
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.AssistChip
//import androidx.compose.material3.AssistChipDefaults
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import com.example.peso.model.Period
//import com.example.peso.model.Transaction
//import java.math.BigDecimal
//import java.math.RoundingMode
//import java.time.Instant
//import java.time.LocalDate
//import java.time.ZoneId
//
///* ======= design tokens ======= */
//val CARD_RADIUS = 16.dp
//val OUTER_PAD = 16.dp
//val INNER_PAD = 16.dp
//
///* ===================== Money / BigDecimal ===================== */
//val BD0: BigDecimal = BigDecimal.ZERO
//fun bd(d: Double): BigDecimal = BigDecimal.valueOf(d)
//
//operator fun BigDecimal.unaryMinus(): BigDecimal = negate()
//operator fun BigDecimal.plus(other: BigDecimal): BigDecimal = add(other)
//operator fun BigDecimal.minus(other: BigDecimal): BigDecimal = subtract(other)
//operator fun BigDecimal.times(other: BigDecimal): BigDecimal = multiply(other)
//operator fun BigDecimal.div(other: BigDecimal): BigDecimal = divide(other, 6, RoundingMode.HALF_UP)
//operator fun BigDecimal.compareTo(other: BigDecimal): Int = compareTo(other)
//
//fun BigDecimal.safeDouble(): Double = toPlainString().toDoubleOrNull() ?: 0.0
//fun BigDecimal.format2(): String = "%,.2f".format(safeDouble())
//fun Double.format2(): String = "%,.2f".format(this)
//
///* ===================== Period labels ===================== */
//val Period.label: String
//    get() = when (this) {
//        Period.DAY -> "Dan"
//        Period.WEEK -> "Teden"
//        Period.MONTH -> "Mesec"
//        Period.YEAR -> "Leto"
//    }
//val Period.labelLower: String
//    get() = when (this) {
//        Period.DAY -> "dan"
//        Period.WEEK -> "teden"
//        Period.MONTH -> "mesec"
//        Period.YEAR -> "leto"
//    }
//
///* ===================== Dates & ranges ===================== */
//private val ZONE: ZoneId = ZoneId.systemDefault()
//fun todayLD(): LocalDate = LocalDate.now(ZONE)
//
//fun currentBounds(p: Period): Pair<LocalDate, LocalDate> = when (p) {
//    Period.DAY -> todayLD() to todayLD()
//    Period.WEEK -> todayLD().with(java.time.DayOfWeek.MONDAY) to todayLD()
//    Period.MONTH -> todayLD().withDayOfMonth(1) to todayLD()
//    Period.YEAR -> todayLD().withDayOfYear(1) to todayLD()
//}
//fun LocalDate.isBetween(a: LocalDate, b: LocalDate): Boolean = !isBefore(a) && !isAfter(b)
//fun LocalDate.pretty(): String = "${dayOfMonth}. ${monthValue}. ${year}"
//
///* Robustna pretvorba ts sekund/milisekund v LocalDate */
//fun Transaction.dateLD(): LocalDate =
//    if (ts < 1_000_000_000_000L)
//        Instant.ofEpochSecond(ts).atZone(ZONE).toLocalDate()
//    else
//        Instant.ofEpochMilli(ts).atZone(ZONE).toLocalDate()
//
///* ===================== Transaction-safe helpers ===================== */
//fun Transaction.amountBD(): BigDecimal = when (val v = runCatching { this.amount }.getOrNull()) {
//    is BigDecimal -> v
//    is Double -> bd(v)
//    is Float -> bd(v.toDouble())
//    is Int -> bd(v.toDouble())
//    else -> BD0
//}
//
//fun Transaction.titleSafe(): String =
//    runCatching { this::class.java.getDeclaredField("title").apply { isAccessible = true }.get(this) as? String }.getOrNull()
//        ?: runCatching { this::class.java.getDeclaredField("name").apply { isAccessible = true }.get(this) as? String }.getOrNull()
//        ?: runCatching { this::class.java.getDeclaredField("merchant").apply { isAccessible = true }.get(this) as? String }.getOrNull()
//        ?: runCatching { this::class.java.getDeclaredField("description").apply { isAccessible = true }.get(this) as? String }.getOrNull()
//        ?: "Transakcija"
//
//fun Transaction.categoryOpt(): String? =
//    runCatching { this::class.java.getDeclaredField("category").apply { isAccessible = true }.get(this) as? String }.getOrNull()
//        ?: runCatching { this::class.java.getDeclaredField("group").apply { isAccessible = true }.get(this) as? String }.getOrNull()
//
//fun Transaction.dateLabelSafe(): String = dateLD().pretty()
//
//fun Transaction.isIn(p: Period): Boolean {
//    val (s, e) = currentBounds(p)
//    val d = dateLD()
//    return d.isBetween(s, e)
//}
//
//fun Transaction.iconOrFallback(): String {
//    val map = mapOf("Hrana" to "🛒", "Prevoz" to "🚗", "Kava" to "☕️", "Zabava" to "🎉")
//    val cat = categoryOpt()
//    return map[cat] ?: if (amountBD() < BD0) "💸" else "💰"
//}
//
///* ===================== UI mini helpers ===================== */
//data class Status(val text: String, val color: Color)
//fun statusFor(progress: Double): Status = when {
//    progress < 0.50 -> Status("Dobro", Color(0xFF2DBE6C))
//    progress < 0.85 -> Status("Zmerno", Color(0xFFE5A94A))
//    else            -> Status("Slabo", Color(0xFFDB6B5E))
//}
//
//@Composable
//fun StatusBadge(s: Status) {
//    Box(
//        Modifier
//            .background(s.color.copy(alpha = .18f), RoundedCornerShape(999.dp))
//            .padding(horizontal = 12.dp, vertical = 6.dp)
//    ) { Text(s.text, color = s.color) }
//}
//
//@Composable
//fun TimeFilterChips(selected: Period, onSelect: (Period) -> Unit) {
//    val opts = listOf(Period.DAY, Period.WEEK, Period.MONTH, Period.YEAR)
//    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
//        opts.forEach { p ->
//            val sel = p == selected
//            AssistChip(
//                onClick = { onSelect(p) },
//                label = { Text(p.label) },
//                colors = AssistChipDefaults.assistChipColors(
//                    containerColor = if (sel)
//                        MaterialTheme.colorScheme.primary.copy(alpha = .22f)
//                    else
//                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = .45f),
//                    labelColor = if (sel)
//                        MaterialTheme.colorScheme.onPrimary
//                    else
//                        MaterialTheme.colorScheme.onSurfaceVariant
//                ),
//                border = null,
//                shape = RoundedCornerShape(12.dp)
//            )
//        }
//    }
//}
