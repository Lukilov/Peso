package com.example.peso.model

import java.time.format.DateTimeFormatter
import java.util.Locale

private val DATE_FMT = DateTimeFormatter.ofPattern("d. MMM yyyy", Locale.getDefault())

val Transaction.displayTitle: String
    get() = if (name.isNotBlank()) name else merchant ?: "Transakcija"

val Transaction.dateLabel: String
    get() = date.format(DATE_FMT)

val Transaction.category: String
    get() = type ?: "Neznano"
