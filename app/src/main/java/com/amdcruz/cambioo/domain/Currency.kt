package com.amdcruz.cambioo.domain

enum class Currency(
    val code: String,
    val displayName: String,
    val spokenName: String,
) {
    USD(code = "USD", displayName = "Dólar", spokenName = "dólar"),
    EUR(code = "EUR", displayName = "Euro", spokenName = "euro"),
    GBP(code = "GBP", displayName = "Libra esterlina", spokenName = "libra esterlina"),
    CHF(code = "CHF", displayName = "Franco suíço", spokenName = "franco suíço");

    companion object {
        val displayOrder: List<Currency> = entries.toList()

        fun fromCode(code: String): Currency? =
            entries.firstOrNull { it.code.equals(code, ignoreCase = true) }
    }
}
