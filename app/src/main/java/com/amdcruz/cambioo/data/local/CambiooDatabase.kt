package com.amdcruz.cambioo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        SnapshotMetaEntity::class,
        RateObservationEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class CambiooDatabase : RoomDatabase() {
    abstract fun snapshotDao(): SnapshotDao
}
