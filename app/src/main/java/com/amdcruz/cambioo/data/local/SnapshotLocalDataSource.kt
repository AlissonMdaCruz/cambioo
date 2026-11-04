package com.amdcruz.cambioo.data.local

import com.amdcruz.cambioo.data.RatesSnapshot
import com.amdcruz.cambioo.domain.Currency
import com.amdcruz.cambioo.domain.RateObservation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

interface SnapshotLocalDataSource {
    fun observeSnapshot(): Flow<RatesSnapshot?>

    suspend fun replaceSnapshot(snapshot: RatesSnapshot)
}

class RoomSnapshotLocalDataSource @Inject constructor(
    private val snapshotDao: SnapshotDao,
) : SnapshotLocalDataSource {
    override fun observeSnapshot(): Flow<RatesSnapshot?> {
        return combine(
            snapshotDao.observeMeta(),
            snapshotDao.observeObservations(),
        ) { meta, observations ->
            if (meta == null) {
                null
            } else {
                RatesSnapshot(
                    savedAt = Instant.ofEpochMilli(meta.savedAtEpochMilli),
                    from = LocalDate.parse(meta.fromDate),
                    to = LocalDate.parse(meta.toDate),
                    sourceName = meta.sourceName,
                    observations = observations.mapNotNull { it.toDomain() },
                )
            }
        }
    }

    override suspend fun replaceSnapshot(snapshot: RatesSnapshot) {
        val meta = SnapshotMetaEntity(
            savedAtEpochMilli = snapshot.savedAt.toEpochMilli(),
            fromDate = snapshot.from.toString(),
            toDate = snapshot.to.toString(),
            sourceName = snapshot.sourceName,
        )
        val entities = snapshot.observations.map { observation ->
            RateObservationEntity(
                currencyCode = observation.currency.code,
                observationDate = observation.date.toString(),
                apiRate = observation.apiRate.toPlainString(),
            )
        }
        snapshotDao.replaceSnapshot(meta = meta, observations = entities)
    }

    private fun RateObservationEntity.toDomain(): RateObservation? {
        val currency = Currency.fromCode(currencyCode) ?: return null
        return RateObservation(
            date = LocalDate.parse(observationDate),
            currency = currency,
            apiRate = BigDecimal(apiRate),
        )
    }
}
