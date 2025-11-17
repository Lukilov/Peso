package com.example.peso.model

import java.math.BigDecimal
import java.time.LocalDate

object FakeData {

    private const val YEAR = 2025

    val transactions: List<Transaction> = buildList {

        // --- Mesečni ponavljajoči se prihodki in stroški čez celo leto ---
        for (month in 1..12) {
            // Prihodki
            add(
                Transaction(
                    name = "Plača",
                    amount = BigDecimal("1200.00"),
                    date = LocalDate.of(YEAR, month, 1),
                    isIncome = true,
                    type = "salary"
                )
            )

            // Občasni freelance prihodki (na 2–3 mesece)
            if (month in listOf(2, 5, 8, 11)) {
                add(
                    Transaction(
                        name = "Freelance projekt",
                        amount = BigDecimal("250.00"),
                        date = LocalDate.of(YEAR, month, 8),
                        isIncome = true,
                        type = "freelance"
                    )
                )
            }

            // Stanovanje in računi
            add(
                Transaction(
                    name = "Najemnina",
                    amount = BigDecimal("400.00"),
                    date = LocalDate.of(YEAR, month, 3),
                    isIncome = false,
                    type = "housing"
                )
            )
            add(
                Transaction(
                    name = "Elektrika",
                    amount = BigDecimal("55.70"),
                    date = LocalDate.of(YEAR, month, 21),
                    isIncome = false,
                    type = "utilities"
                )
            )
            add(
                Transaction(
                    name = "Internet",
                    amount = BigDecimal("29.99"),
                    date = LocalDate.of(YEAR, month, 22),
                    isIncome = false,
                    type = "utilities"
                )
            )
            add(
                Transaction(
                    name = "Voda",
                    amount = BigDecimal("18.60"),
                    date = LocalDate.of(YEAR, month, 23),
                    isIncome = false,
                    type = "utilities"
                )
            )

            // Hrana in pijača
            add(
                Transaction(
                    name = "Nakup hrane",
                    amount = BigDecimal("45.30"),
                    date = LocalDate.of(YEAR, month, 5),
                    isIncome = false,
                    type = "food"
                )
            )
            add(
                Transaction(
                    name = "Restavracija",
                    amount = BigDecimal("27.50"),
                    date = LocalDate.of(YEAR, month, 15),
                    isIncome = false,
                    type = "food"
                )
            )
            add(
                Transaction(
                    name = "Hitri prigrizek",
                    amount = BigDecimal("6.90"),
                    date = LocalDate.of(YEAR, month, 10),
                    isIncome = false,
                    type = "food"
                )
            )
            add(
                Transaction(
                    name = "Kava",
                    amount = BigDecimal("3.80"),
                    date = LocalDate.of(YEAR, month, 4),
                    isIncome = false,
                    type = "drink"
                )
            )
            add(
                Transaction(
                    name = "Kava s prijateljem",
                    amount = BigDecimal("4.20"),
                    date = LocalDate.of(YEAR, month, 18),
                    isIncome = false,
                    type = "drink"
                )
            )

            // Prevoz
            add(
                Transaction(
                    name = "Gorivo",
                    amount = BigDecimal("60.00"),
                    date = LocalDate.of(YEAR, month, 12),
                    isIncome = false,
                    type = "transport"
                )
            )
            add(
                Transaction(
                    name = "Avtobusna karta",
                    amount = BigDecimal("2.40"),
                    date = LocalDate.of(YEAR, month, 7),
                    isIncome = false,
                    type = "transport"
                )
            )
            if (month in listOf(3, 9)) {
                add(
                    Transaction(
                        name = "Servis kolesa",
                        amount = BigDecimal("15.00"),
                        date = LocalDate.of(YEAR, month, 14),
                        isIncome = false,
                        type = "transport"
                    )
                )
            }

            // Naročnine in zabava
            add(
                Transaction(
                    name = "Netflix",
                    amount = BigDecimal("7.99"),
                    date = LocalDate.of(YEAR, month, 9),
                    isIncome = false,
                    type = "subscription"
                )
            )
            add(
                Transaction(
                    name = "Spotify",
                    amount = BigDecimal("5.99"),
                    date = LocalDate.of(YEAR, month, 11),
                    isIncome = false,
                    type = "subscription"
                )
            )
            if (month in listOf(1, 4, 7, 10)) {
                add(
                    Transaction(
                        name = "Kino",
                        amount = BigDecimal("9.50"),
                        date = LocalDate.of(YEAR, month, 20),
                        isIncome = false,
                        type = "entertainment"
                    )
                )
            }

            // Šolanje / knjige
            if (month in listOf(1, 9)) {
                add(
                    Transaction(
                        name = "Knjižnica - članarina",
                        amount = BigDecimal("3.00"),
                        date = LocalDate.of(YEAR, month, 2),
                        isIncome = false,
                        type = "education"
                    )
                )
            }

            // Zdravje in šport
            add(
                Transaction(
                    name = "Trening fitnes",
                    amount = BigDecimal("30.00"),
                    date = LocalDate.of(YEAR, month, 14),
                    isIncome = false,
                    type = "fitness"
                )
            )
            if (month in listOf(2, 5, 11)) {
                add(
                    Transaction(
                        name = "Lekarna",
                        amount = BigDecimal("12.40"),
                        date = LocalDate.of(YEAR, month, 6),
                        isIncome = false,
                        type = "health"
                    )
                )
            }

            // Shopping / oblačila
            if (month in listOf(3, 6, 9, 12)) {
                add(
                    Transaction(
                        name = "Nova majica",
                        amount = BigDecimal("19.99"),
                        date = LocalDate.of(YEAR, month, 17),
                        isIncome = false,
                        type = "shopping"
                    )
                )
            }

            // Darila
            if (month in listOf(2, 6, 10, 12)) {
                add(
                    Transaction(
                        name = "Darilo prijatelju",
                        amount = BigDecimal("25.00"),
                        date = LocalDate.of(YEAR, month, 24),
                        isIncome = false,
                        type = "gift"
                    )
                )
            }
        }

        // --- Nekaj enkratnih posebnih prihodkov čez leto ---
        add(
            Transaction(
                name = "Prodaja starega telefona",
                amount = BigDecimal("180.00"),
                date = LocalDate.of(YEAR, 3, 8),
                isIncome = true,
                type = "sale"
            )
        )
        add(
            Transaction(
                name = "Prodaja računalniškega monitorja",
                amount = BigDecimal("90.00"),
                date = LocalDate.of(YEAR, 7, 19),
                isIncome = true,
                type = "sale"
            )
        )
        add(
            Transaction(
                name = "Enkratno plačilo za projekt",
                amount = BigDecimal("320.00"),
                date = LocalDate.of(YEAR, 10, 5),
                isIncome = true,
                type = "freelance"
            )
        )
    }
}
