package com.amdcruz.cambioo.data

import com.amdcruz.cambioo.domain.RateObservation
import java.time.Instant
import java.time.LocalDate

data class RatesSnapshot(
    val savedAt: Instant,
    val from: LocalDate,
    val to: LocalDate,
    val sourceName: String,
    val observations: List<RateObservation>,
)

sealed interface RefreshResult {
    data class Success(val snapshot: RatesSnapshot) : RefreshResult
    data object Empty : RefreshResult
    data class Failure(val cause: Throwable) : RefreshResult
}
