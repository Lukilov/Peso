package com.example.peso.model

import java.math.BigDecimal
import java.time.LocalDate

data class Transaction(
    val name: String,
    val amount: BigDecimal,
    val date: LocalDate,
    val isIncome: Boolean,
    val merchant: String? = null,
    val note: String? = null,
    val type: String? = null,
    val ts: Long = System.currentTimeMillis()
)
