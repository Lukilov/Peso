package com.example.peso.model

enum class Period { DAY, WEEK, MONTH, YEAR;

    val label: String get() = when (this) {
        DAY -> "Dan"; WEEK -> "Teden"; MONTH -> "Mesec"; YEAR -> "Leto"
    }
    val labelLower: String get() = when (this) {
        DAY -> "dan"; WEEK -> "teden"; MONTH -> "mesec"; YEAR -> "leto"
    }
}
