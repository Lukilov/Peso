package com.example.peso.model

import java.math.BigDecimal
import java.math.RoundingMode

fun BigDecimal.format2(): String =
    this.setScale(2, RoundingMode.HALF_UP).toPlainString()
