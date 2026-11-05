package com.amdcruz.cambioo.domain

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.ZoneId

object QuoteMath {
    val SCALE: Int = 2
    val ROUNDING: RoundingMode = RoundingMode.HALF_UP
    val SAO_PAULO: ZoneId = ZoneId.of("America/Sao_Paulo")
    private val ONE: BigDecimal = BigDecimal.ONE
    private val HUNDRED: BigDecimal = BigDecimal("100")

    fun quoteFromApiRate(apiRate: BigDecimal): BigDecimal? {
        if (apiRate.signum() <= 0) {
            return null
        }
        return ONE.divide(apiRate, SCALE, ROUNDING)
    }

    fun variationPercent(current: BigDecimal, previous: BigDecimal): BigDecimal {
        return current
            .subtract(previous)
            .divide(previous, SCALE + 4, ROUNDING)
            .multiply(HUNDRED)
            .setScale(SCALE, ROUNDING)
    }

    fun toAppQuote(observation: RateObservation): AppQuote? {
        val value = quoteFromApiRate(observation.apiRate) ?: return null
        return AppQuote(
            currency = observation.currency,
            date = observation.date,
            valueInReais = value,
        )
    }
}

data class WindowDates(
    val from: LocalDate,
    val to: LocalDate,
) {
    companion object {
        fun forLocalDate(today: LocalDate): WindowDates {
            return WindowDates(
                from = today.minusDays(29),
                to = today,
            )
        }
    }
}
