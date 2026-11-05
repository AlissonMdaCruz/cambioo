package com.amdcruz.cambioo.data.remote

import com.amdcruz.cambioo.domain.Currency
import com.amdcruz.cambioo.domain.RateObservation
import com.amdcruz.cambioo.domain.WindowDates
import java.math.BigDecimal
import java.time.LocalDate
import javax.inject.Inject

interface RatesRemoteDataSource {
    suspend fun fetchWindow(window: WindowDates): List<RateObservation>
}

class FrankfurterRatesRemoteDataSource @Inject constructor(
    private val api: FrankfurterApi,
) : RatesRemoteDataSource {
    override suspend fun fetchWindow(window: WindowDates): List<RateObservation> {
        val dtoList = api.fetchWindowRates(
            from = window.from.toString(),
            to = window.to.toString(),
        )
        return dtoList.mapNotNull { dto -> dto.toDomain() }
    }

    private fun FrankfurterRateDto.toDomain(): RateObservation? {
        val currency = Currency.fromCode(quote) ?: return null
        val rate = runCatching { BigDecimal(rate) }.getOrNull() ?: return null
        val observationDate = runCatching { LocalDate.parse(date) }.getOrNull() ?: return null
        return RateObservation(
            date = observationDate,
            currency = currency,
            apiRate = rate,
        )
    }
}
