package com.amdcruz.cambioo.data

import kotlinx.coroutines.flow.Flow

interface RatesRepository {
    fun observeSnapshot(): Flow<RatesSnapshot?>

    suspend fun refreshWindow(): RefreshResult
}
