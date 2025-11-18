package com.example.peso.notifications

/**
 * Lokalni "AI" klasifikator:
 * - iz besedila notifikacije ugotovi, ali je priliv ali odliv
 * - na podlagi trgovca / ključnih besed določi kategorijo (hrana, prevoz, ...).
 */
object LocalBankClassifier {

    data class Result(
        val category: String,
        val isIncome: Boolean
    )

    // preprosta pravila – PO POTREBI DODAJ SVOJE
    private data class Rule(val regex: Regex, val category: String)

    private val foodRules = listOf(
        Rule(Regex("mercator|spar|hofer|lidl|tu[šs]", RegexOption.IGNORE_CASE), "Hrana - trgovina"),
        Rule(Regex("mcdonald|burger king|kfc|subway|pizza", RegexOption.IGNORE_CASE), "Hrana - restavracija")
    )

    private val drinkRules = listOf(
        Rule(Regex("bar|caffe|kava|coffee|starbucks", RegexOption.IGNORE_CASE), "Pijača")
    )

    private val transportRules = listOf(
        Rule(Regex("petrol|omv|mol|shell|bencin|gorivo|talna karta|bus|avtobus", RegexOption.IGNORE_CASE), "Prevoz")
    )

    private val funRules = listOf(
        Rule(Regex("cinema|kino|spotify|netflix|hbo", RegexOption.IGNORE_CASE), "Zabava")
    )

    private val allRules = foodRules + drinkRules + transportRules + funRules

    fun classify(
        app: String,
        title: String,
        text: String,
        merchantHint: String?
    ): Result {
        val joined = buildString {
            append(app).append(' ')
            append(title).append(' ')
            append(text).append(' ')
            if (!merchantHint.isNullOrBlank()) append(merchantHint)
        }

        val lower = joined.lowercase()

        // 1) ugotovi, ali je priliv/odliv
        val isIncome = isIncome(lower)

        // 2) najdi prvo ujemanje pravila
        val ruleMatch = allRules.firstOrNull { it.regex.containsMatchIn(joined) }

        val category = ruleMatch?.category ?: run {
            // fallback: če nič ne matcha, malo pametno ugibamo
            when {
                "najemnina" in lower || "rent" in lower -> "Stanovanje"
                "trgovina" in lower || "shop" in lower  -> "Nakup"
                else                                    -> "Drugo"
            }
        }

        return Result(category = category, isIncome = isIncome)
    }

    private fun isIncome(text: String): Boolean {
        // besede, ki običajno pomenijo priliv
        val incomeKeywords = listOf(
            "prejem", "nakazilo", "priliv", "plača", "salary",
            "income", "polog v dobro", "credited"
        )
        // besede, ki običajno pomenijo odliv
        val expenseKeywords = listOf(
            "pos nakup", "d dvig", "dvig gotovine", "nakup", "bremenitev",
            "card purchase", "payment", "plačilo"
        )

        val hasIncomeWord = incomeKeywords.any { it in text }
        val hasExpenseWord = expenseKeywords.any { it in text }

        return when {
            hasIncomeWord && !hasExpenseWord -> true
            hasExpenseWord && !hasIncomeWord -> false
            else -> false // default: obravnavaj kot odliv
        }
    }
}
