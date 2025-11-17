package com.example.peso.model

import androidx.lifecycle.ViewModel
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

class BudgetViewModel : ViewModel() {

    // VSE transakcije – source of truth
    val transactions: List<Transaction> = FakeData.transactions

    var monthlyLimit: BigDecimal? = BigDecimal("800.00")

    private val all: List<Transaction> = transactions

    fun totalFor(period: Period): BigDecimal =
        sumFor(period, includeIncome = true, includeExpense = true)

    fun incomeFor(period: Period): BigDecimal =
        sumFor(period, includeIncome = true, includeExpense = false)

    fun expenseFor(period: Period): BigDecimal =
        sumFor(period, includeIncome = false, includeExpense = true)

    fun budgetProgress(period: Period): Float {
        val limit = monthlyLimit ?: return 0f
        val spent = expenseFor(Period.MONTH)
        if (limit <= BigDecimal.ZERO) return 0f
        return spent.divide(limit, 4, RoundingMode.HALF_UP).toFloat()
    }

    fun seriesFor(period: Period): List<Float> {
        val (start, end) = when (period) {
            Period.DAY -> LocalDate.now() to LocalDate.now()
            Period.WEEK -> LocalDate.now().minusDays(6) to LocalDate.now()
            Period.MONTH -> LocalDate.now().withDayOfMonth(1) to LocalDate.now()
            Period.YEAR -> LocalDate.now().withDayOfYear(1) to LocalDate.now()
        }

        val dates = generateSequence(start) { d ->
            d.plusDays(1).takeIf { it <= end }
        }.toList()

        val sumsByDate = dates.associateWith { BigDecimal.ZERO }.toMutableMap()

        // samo odhodki v danem obdobju
        all.filter { !it.isIncome && it.date in start..end }.forEach { t ->
            sumsByDate[t.date] =
                (sumsByDate[t.date] ?: BigDecimal.ZERO) + t.amount.abs()
        }

        return dates.map { (sumsByDate[it] ?: BigDecimal.ZERO).toFloat() }
    }

    private fun sumFor(
        period: Period,
        includeIncome: Boolean,
        includeExpense: Boolean
    ): BigDecimal {
        val filtered = all.filter { it.isIn(period) }
        return filtered.fold(BigDecimal.ZERO) { acc, t ->
            when {
                t.isIncome && includeIncome -> acc + t.amount
                !t.isIncome && includeExpense -> acc + t.amount
                else -> acc
            }
        }
    }
}
