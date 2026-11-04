package com.amdcruz.cambioo.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "snapshot_meta")
data class SnapshotMetaEntity(
    @PrimaryKey val id: Int = SINGLE_ID,
    val savedAtEpochMilli: Long,
    val fromDate: String,
    val toDate: String,
    val sourceName: String,
) {
    companion object {
        const val SINGLE_ID: Int = 1
    }
}

@Entity(
    tableName = "rate_observations",
    primaryKeys = ["currencyCode", "observationDate"],
)
data class RateObservationEntity(
    val currencyCode: String,
    val observationDate: String,
    val apiRate: String,
)
