package com.amdcruz.cambioo.domain

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.Instant
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object QuoteFormatter {
    private val PT_BR: Locale = Locale.forLanguageTag("pt-BR")
    private val MINUS_SIGN: Char = '\u2212'
    private val EM_DASH: String = "\u2014"
    private val DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", PT_BR)
    private val DATE_TIME_FORMAT: DateTimeFormatter =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", PT_BR)
    private val LONG_DATE_FORMAT: DateTimeFormatter =
        DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", PT_BR)
    private val PERCENT_FORMAT: DecimalFormat = DecimalFormat("0.00", DecimalFormatSymbols(PT_BR))

    fun formatMoneyVisual(value: BigDecimal): String {
        val scaled = value.setScale(QuoteMath.SCALE, RoundingMode.HALF_UP)
        val symbols = DecimalFormatSymbols(PT_BR)
        val format = DecimalFormat("0.00", symbols)
        return "R$ ${format.format(scaled)}"
    }

    fun formatVariationVisual(variationPercent: BigDecimal?): String {
        if (variationPercent == null) {
            return EM_DASH
        }
        val scaled = variationPercent.setScale(QuoteMath.SCALE, RoundingMode.HALF_UP)
        val absolute = PERCENT_FORMAT.format(scaled.abs())
        return when (scaled.signum()) {
            1 -> "+$absolute%"
            -1 -> "$MINUS_SIGN$absolute%"
            else -> "0,00%"
        }
    }

    fun formatMoneySpoken(currency: Currency, value: BigDecimal): String {
        val amount = formatAmountInWords(value)
        return "${currency.spokenName}, $amount"
    }

    fun formatVariationSpoken(variationPercent: BigDecimal?): String? {
        if (variationPercent == null) {
            return null
        }
        val scaled = variationPercent.setScale(QuoteMath.SCALE, RoundingMode.HALF_UP)
        val absolute = PERCENT_FORMAT.format(scaled.abs())
        return when (scaled.signum()) {
            1 -> "alta de $absolute por cento"
            -1 -> "queda de $absolute por cento"
            else -> "estável"
        }
    }

    fun formatObservationDate(date: LocalDate): String = date.format(DATE_FORMAT)

    fun formatObservationDateSpoken(date: LocalDate): String = date.format(LONG_DATE_FORMAT)

    fun formatSavedAt(instant: Instant): String {
        val zoned: ZonedDateTime = instant.atZone(QuoteMath.SAO_PAULO)
        return zoned.format(DATE_TIME_FORMAT)
    }

    fun isTodayInSaoPaulo(date: LocalDate, today: LocalDate): Boolean = date == today

    fun formatAmountInWords(value: BigDecimal): String {
        val scaled = value.setScale(QuoteMath.SCALE, RoundingMode.HALF_UP)
        val reais = scaled.toBigInteger().toInt()
        val centavos = scaled
            .remainder(BigDecimal.ONE)
            .movePointRight(2)
            .abs()
            .toInt()
        return when {
            reais == 0 && centavos == 0 -> "0 reais"
            reais == 0 -> formatCentavos(centavos)
            centavos == 0 -> formatReais(reais)
            else -> "${formatReais(reais)} e ${formatCentavos(centavos)}"
        }
    }

    private fun formatReais(reais: Int): String =
        if (reais == 1) "1 real" else "$reais reais"

    private fun formatCentavos(centavos: Int): String =
        if (centavos == 1) "1 centavo" else "$centavos centavos"
}
