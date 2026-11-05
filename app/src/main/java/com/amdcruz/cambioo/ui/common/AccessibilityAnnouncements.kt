package com.amdcruz.cambioo.ui.common

/**
 * Builds TalkBack announcements so the spoken quote is heard once,
 * without appending the raw visual amount (e.g. "R$ 5,42").
 */
object AccessibilityAnnouncements {
    fun quoteRow(
        valueSpoken: String,
        variationSpoken: String?,
    ): String {
        return if (variationSpoken.isNullOrBlank()) {
            valueSpoken
        } else {
            "$valueSpoken, $variationSpoken"
        }
    }

    fun historyRow(
        observationDateSpoken: String,
        valueSpoken: String,
        variationSpoken: String?,
    ): String {
        return if (variationSpoken.isNullOrBlank()) {
            "$observationDateSpoken, $valueSpoken"
        } else {
            "$observationDateSpoken, $valueSpoken, $variationSpoken"
        }
    }
}
