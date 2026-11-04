package com.amdcruz.cambioo.data

import com.amdcruz.cambioo.data.remote.RatesRemoteDataSource
import com.amdcruz.cambioo.domain.RateObservation
import com.amdcruz.cambioo.domain.WindowDates

class FakeRatesRemoteDataSource : RatesRemoteDataSource {
    var observationsToReturn: List<RateObservation> = emptyList()
    var errorToThrow: Throwable? = null
    var lastWindow: WindowDates? = null
        private set
    var fetchCallCount: Int = 0
        private set

    override suspend fun fetchWindow(window: WindowDates): List<RateObservation> {
        fetchCallCount += 1
        lastWindow = window
        errorToThrow?.let { throw it }
        return observationsToReturn
    }
}
