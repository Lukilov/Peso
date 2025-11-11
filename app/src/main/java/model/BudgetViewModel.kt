package com.example.peso.model

import androidx.lifecycle.ViewModel
import java.math.BigDecimal
import java.time.LocalDate

class BudgetViewModel : ViewModel() {

    // 💰 seznam transakcij (dodano!)
    val transactions = listOf(
        Transaction("Nakup hrane", BigDecimal("12.5"), LocalDate.parse("2025-11-10"), isIncome = false),
        Transaction("Kava", BigDecimal("2.0"), LocalDate.parse("2025-11-09"), isIncome = false),
        Transaction("Plača", BigDecimal("1200.0"), LocalDate.parse("2025-11-01"), isIncome = true)
    )

    var monthlyLimit: BigDecimal? = BigDecimal("800.00")

    private val all = transactions

    fun totalFor(period: Period): BigDecimal =
        sumFor(period, includeIncome = true, includeExpense = true)

    fun incomeFor(period: Period): BigDecimal =
        sumFor(period, includeIncome = true, includeExpense = false)

    fun expenseFor(period: Period): BigDecimal =
        sumFor(period, includeIncome = false, includeExpense = true)

    fun remainingAgainstLimit(period: Period): BigDecimal? =
        monthlyLimit?.let { it - expenseFor(Period.MONTH) }

    fun budgetProgress(period: Period): Float {
        val limit = monthlyLimit ?: return 0f
        val spent = expenseFor(Period.MONTH)
        if (limit <= BigDecimal.ZERO) return 0f
        return spent.divide(limit, 4, java.math.RoundingMode.HALF_UP).toFloat()
    }

    fun seriesFor(period: Period): List<Float> {
        val dates = window(period)
        val sumsByDate = dates.associateWith { BigDecimal.ZERO }.toMutableMap()
        val filtered = filterByPeriod(all, period)
        filtered.filter { !it.isIncome }.forEach { t ->
            sumsByDate[t.date] = (sumsByDate[t.date] ?: BigDecimal.ZERO) + t.amount
        }
        return dates.map { (sumsByDate[it] ?: BigDecimal.ZERO).toFloat() }
    }

    private fun sumFor(period: Period, includeIncome: Boolean, includeExpense: Boolean): BigDecimal {
        val filtered = filterByPeriod(all, period)
        return filtered.fold(BigDecimal.ZERO) { acc, t ->
            when {
                t.isIncome && includeIncome -> acc + t.amount
                !t.isIncome && includeExpense -> acc + t.amount
                else -> acc
            }
        }
    }

    private fun filterByPeriod(list: List<Transaction>, period: Period): List<Transaction> {
        val (start, end) = when (period) {
            Period.DAY -> LocalDate.now() to LocalDate.now()
            Period.WEEK -> LocalDate.now().minusDays(6) to LocalDate.now()
            Period.MONTH -> LocalDate.now().withDayOfMonth(1) to LocalDate.now()
            Period.YEAR -> LocalDate.now().withDayOfYear(1) to LocalDate.now()
        }
        return list.filter { it.date >= start && it.date <= end }
    }

    private fun window(period: Period): List<LocalDate> {
        val (start, end) = when (period) {
            Period.DAY -> LocalDate.now() to LocalDate.now()
            Period.WEEK -> LocalDate.now().minusDays(6) to LocalDate.now()
            Period.MONTH -> LocalDate.now().withDayOfMonth(1) to LocalDate.now()
            Period.YEAR -> LocalDate.now().withDayOfYear(1) to LocalDate.now()
        }
        return generateSequence(start) { d -> d.plusDays(1).takeIf { it <= end } }.toList()
    }
}
