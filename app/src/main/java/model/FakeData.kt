package com.example.peso.model

import java.math.BigDecimal
import java.time.LocalDate

object FakeData {

    val transactions = listOf(
        Transaction(
            name = "Nakup hrane",
            amount = BigDecimal("12.50"),
            date = LocalDate.of(2025, 11, 10),
            isIncome = false,
            type = "food"
        ),
        Transaction(
            name = "Kava",
            amount = BigDecimal("2.00"),
            date = LocalDate.of(2025, 11, 9),
            isIncome = false,
            type = "drink"
        ),
        Transaction(
            name = "Plača",
            amount = BigDecimal("1200.00"),
            date = LocalDate.of(2025, 11, 1),
            isIncome = true,
            type = "salary"
        )
    )
}
