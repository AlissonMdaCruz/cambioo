package com.amdcruz.cambioo.domain

import java.math.BigDecimal
import java.time.LocalDate

data class RateObservation(
    val date: LocalDate,
    val currency: Currency,
    val apiRate: BigDecimal,
)

data class AppQuote(
    val currency: Currency,
    val date: LocalDate,
    val valueInReais: BigDecimal,
)

data class QuotedObservation(
    val quote: AppQuote,
    val variationPercent: BigDecimal?,
)
