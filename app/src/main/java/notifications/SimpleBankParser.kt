package com.example.peso.notifications

import java.math.BigDecimal

/**
 * Parser za bančne SMS / notifikacije.
 * 1. Najprej poskusi specifičen OTP-banka format (POS NAKUP ... znesek 12,00 EUR, GOSTILNA ...).
 * 2. Če ne uspe, uporabi generične regexe.
 */
object SimpleBankParser {

    // OTP banka – primer:
    // POS NAKUP 17.11.2025 21:42, kartica ***0971,
    // znesek 12,00 EUR, GOSTILNA JARH, LJUBLJANA - S SI. Info: ... OTP banka
    private val otpRegex = Regex(
        pattern = """(?i)pos\s+nakup\s+\d{2}\.\d{2}\.\d{4}\s+\d{2}:\d{2}.*,?\s*kartica.*?znesek\s+(\d+(?:[.,]\d{1,2})?)\s*eur,\s*([^,]+)""",
        option = RegexOption.IGNORE_CASE
    )

    // generične variante:
    // "12,00 EUR", "EUR 12,00", "€ 12.00"
    private val amountPatterns = listOf(
        Regex("""(?i)(?:€\s*|eur\s*)(\d+(?:[.,]\d{1,2})?)"""),     // EUR 12,00
        Regex("""(?i)(\d+(?:[.,]\d{1,2})?)\s*(?:€|eur)""")        // 12,00 EUR
    )

    // generičen trgovec: za npr. "pri MERCATOR LJUBLJANA"
    private val merchantGeneric =
        Regex("""(?i)(?:pri|at|merchant|trgovec)\s*[:\-]?\s*([A-Z0-9\-\s&\.]{3,})""")

    data class Parsed(
        val amount: BigDecimal?,
        val merchant: String?
    )

    fun parse(title: String, text: String): Parsed {
        val joined = "$title $text"

        // 1) Najprej poskusi natančen OTP-banka format
        otpRegex.find(joined)?.let { m ->
            val rawAmount = m.groupValues[1]
            val merchant = m.groupValues[2].trim()   // "GOSTILNA JARH"
            val amount = normalizeAmount(rawAmount)
            return Parsed(amount = amount, merchant = merchant)
        }

        // 2) Če ni OTP, poskusi generične regexe
        val amountStr = amountPatterns
            .firstNotNullOfOrNull { it.find(joined)?.groupValues?.getOrNull(1) }

        val amount = amountStr?.let { normalizeAmount(it) }

        val merchant = merchantGeneric.find(joined)
            ?.groupValues
            ?.getOrNull(1)
            ?.trim()

        return Parsed(amount = amount, merchant = merchant)
    }

    /**
     * Pretvori EU oblike v BigDecimal:
     * "12,00" -> 12.00
     * "1.234,56" -> 1234.56
     */
    private fun normalizeAmount(raw: String): BigDecimal? {
        val cleaned = raw.trim()

        // EU stil: pike kot tisočice, vejica kot decimalka
        val normalized = cleaned
            .replace(".", "")   // odstrani tisočice
            .replace(",", ".")  // decimalno ločilo

        return normalized.toBigDecimalOrNull()
    }
}
