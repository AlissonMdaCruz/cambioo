package com.amdcruz.cambioo.di

import android.content.Context
import androidx.room.Room
import com.amdcruz.cambioo.data.local.CambiooDatabase
import com.amdcruz.cambioo.data.local.RoomSnapshotLocalDataSource
import com.amdcruz.cambioo.data.local.SnapshotDao
import com.amdcruz.cambioo.data.local.SnapshotLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): CambiooDatabase {
        return Room.databaseBuilder(
            context,
            CambiooDatabase::class.java,
            "cambioo.db",
        ).build()
    }

    @Provides
    fun provideSnapshotDao(database: CambiooDatabase): SnapshotDao = database.snapshotDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseBindModule {
    @Binds
    @Singleton
    abstract fun bindSnapshotLocalDataSource(
        impl: RoomSnapshotLocalDataSource,
    ): SnapshotLocalDataSource
}
