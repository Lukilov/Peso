package com.example.peso.model

import java.math.BigDecimal
import java.time.LocalDate

data class Transaction(
    val name: String,
    val amount: BigDecimal,
    val date: LocalDate,
    val isIncome: Boolean,
    val merchant: String? = null,
    val type: String? = null, // tukaj se lahko uporabi kategorija
    val category: String = "Drugo" // privzeta kategorija
)


fun Transaction.isIn(period: Period): Boolean {
    val (start, end) = when (period) {
        Period.DAY -> LocalDate.now() to LocalDate.now()
        Period.WEEK -> LocalDate.now().minusDays(6) to LocalDate.now()
        Period.MONTH -> LocalDate.now().withDayOfMonth(1) to LocalDate.now()
        Period.YEAR -> LocalDate.now().withDayOfYear(1) to LocalDate.now()
    }
    return date in start..end
}
