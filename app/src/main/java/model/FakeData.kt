package com.example.peso.model

import java.math.BigDecimal
import java.time.LocalDate

object FakeData {
    val transactions = listOf(
        // Prihodki
        Transaction(name = "Plača", amount = BigDecimal("1200.00"), date = LocalDate.of(2025, 11, 1), isIncome = true, type = "salary"),
        Transaction(name = "Freelance projekt", amount = BigDecimal("250.00"), date = LocalDate.of(2025, 11, 8), isIncome = true, type = "freelance"),
        Transaction(name = "Prodaja starega telefona", amount = BigDecimal("180.00"), date = LocalDate.of(2025, 11, 5), isIncome = true, type = "sale"),

        // Stroški hrane in pijače
        Transaction(name = "Nakup hrane", amount = BigDecimal("45.30"), date = LocalDate.of(2025, 11, 10), isIncome = false, type = "food"),
        Transaction(name = "Kava", amount = BigDecimal("3.80"), date = LocalDate.of(2025, 11, 6), isIncome = false, type = "drink"),
        Transaction(name = "Restavracija", amount = BigDecimal("27.50"), date = LocalDate.of(2025, 11, 7), isIncome = false, type = "food"),
        Transaction(name = "Hitri prigrizek", amount = BigDecimal("6.90"), date = LocalDate.of(2025, 11, 3), isIncome = false, type = "food"),

        // Prevoz
        Transaction(name = "Gorivo", amount = BigDecimal("60.00"), date = LocalDate.of(2025, 11, 9), isIncome = false, type = "transport"),
        Transaction(name = "Avtobusna karta", amount = BigDecimal("2.40"), date = LocalDate.of(2025, 11, 4), isIncome = false, type = "transport"),
        Transaction(name = "Servis kolesa", amount = BigDecimal("15.00"), date = LocalDate.of(2025, 11, 2), isIncome = false, type = "transport"),

        // Stanovanje in računi
        Transaction(name = "Najemnina", amount = BigDecimal("400.00"), date = LocalDate.of(2025, 11, 1), isIncome = false, type = "housing"),
        Transaction(name = "Elektrika", amount = BigDecimal("55.70"), date = LocalDate.of(2025, 11, 11), isIncome = false, type = "utilities"),
        Transaction(name = "Internet", amount = BigDecimal("29.99"), date = LocalDate.of(2025, 11, 12), isIncome = false, type = "utilities"),
        Transaction(name = "Voda", amount = BigDecimal("18.60"), date = LocalDate.of(2025, 11, 8), isIncome = false, type = "utilities"),

        // Naročnine in zabava
        Transaction(name = "Netflix", amount = BigDecimal("7.99"), date = LocalDate.of(2025, 11, 5), isIncome = false, type = "subscription"),
        Transaction(name = "Spotify", amount = BigDecimal("5.99"), date = LocalDate.of(2025, 11, 6), isIncome = false, type = "subscription"),
        Transaction(name = "Kino", amount = BigDecimal("9.50"), date = LocalDate.of(2025, 11, 10), isIncome = false, type = "entertainment"),
        Transaction(name = "Knjižnica - članarina", amount = BigDecimal("3.00"), date = LocalDate.of(2025, 11, 2), isIncome = false, type = "education"),

        // Drugo
        Transaction(name = "Darilo prijatelju", amount = BigDecimal("25.00"), date = LocalDate.of(2025, 11, 9), isIncome = false, type = "gift"),
        Transaction(name = "Lekarna", amount = BigDecimal("12.40"), date = LocalDate.of(2025, 11, 4), isIncome = false, type = "health"),
        Transaction(name = "Trening fitnes", amount = BigDecimal("30.00"), date = LocalDate.of(2025, 11, 11), isIncome = false, type = "fitness"),
        Transaction(name = "Nova majica", amount = BigDecimal("19.99"), date = LocalDate.of(2025, 11, 7), isIncome = false, type = "shopping")
    )
}
