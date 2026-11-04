package com.amdcruz.cambioo.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface SnapshotDao {
    @Query("SELECT * FROM snapshot_meta WHERE id = 1 LIMIT 1")
    fun observeMeta(): Flow<SnapshotMetaEntity?>

    @Query("SELECT * FROM rate_observations")
    fun observeObservations(): Flow<List<RateObservationEntity>>

    @Query("SELECT * FROM snapshot_meta WHERE id = 1 LIMIT 1")
    suspend fun getMeta(): SnapshotMetaEntity?

    @Query("SELECT * FROM rate_observations")
    suspend fun getObservations(): List<RateObservationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeta(meta: SnapshotMetaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertObservations(observations: List<RateObservationEntity>)

    @Query("DELETE FROM rate_observations")
    suspend fun deleteObservations()

    @Query("DELETE FROM snapshot_meta")
    suspend fun deleteMeta()

    @Transaction
    suspend fun replaceSnapshot(
        meta: SnapshotMetaEntity,
        observations: List<RateObservationEntity>,
    ) {
        deleteObservations()
        deleteMeta()
        insertMeta(meta)
        insertObservations(observations)
    }
}
